/*
Copyright 2010-2013 Michael Shick

This file is part of 'Lock Pattern Generator'.

'Lock Pattern Generator' is free software: you can redistribute it and/or
modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or (at your option)
any later version.

'Lock Pattern Generator' is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details.

You should have received a copy of the GNU General Public License along with
'Lock Pattern Generator'.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.xiaotian.framework.widget.pattern;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name ViewLockPattern
 * @description Pattern 图案匹配视图View
 * @date 2013-10-17
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2013 小天天 Studio, All Rights Reserved.
 */
public class ViewLockPattern extends View {
	public static final int DEFAULT_LENGTH_PX = 100;
	public static final int DEFAULT_LENGTH_NODES = 3;
	public static final float CELL_NODE_RATIO = 0.75f;
	public static final float NODE_EDGE_RATIO = 0.33f;
	public static final int EDGE_COLOR = 0xffbadf06; // 匹配线
	public static final int BACKGROUND_COLOR = 0xff000000;
	public static final int DEATH_COLOR = 0xffff0000; // 匹配失败
	public static final int PRACTICE_RESULT_DISPLAY_MILLIS = 1 * 1000;
	public static final long BUILD_TIMEOUT_MILLIS = 1 * 1000;
	public static final int TACTILE_FEEDBACK_DURATION = 35;

	int mLengthPx;
	int mCellLength;
	int mLengthNodes;
	NodeDrawable[][] mNodeDrawables;
	HighlightMode mHighlightMode;
	boolean mPracticeMode;
	Point mTouchPoint;
	Paint mEdgePaint;
	Point mTouchCell;
	int mTouchThreshold;
	boolean mDrawTouchExtension;
	boolean mDisplayingPracticeResult;
	HighlightMode mPracticeFailureMode;
	HighlightMode mPracticeSuccessMode;
	HighlightMode mPracticeFinishMode;
	PatternListener patternListener;
	boolean mTactileFeedback;
	boolean patternFinish;
	Vibrator mVibrator;
	Handler mHandler;
	//
	List<Point> mCurrentPattern;
	List<Point> mPracticePattern;
	Set<Point> mPracticePool;
	CenterIterator patternPx;

	public ViewLockPattern(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	protected void init() {
		mLengthPx = DEFAULT_LENGTH_PX;
		mLengthNodes = DEFAULT_LENGTH_NODES;
		mNodeDrawables = new NodeDrawable[0][0];
		mCurrentPattern = Collections.emptyList();
		mHighlightMode = new NoHighlight();
		mTouchPoint = new Point(-1, -1);
		mTouchCell = new Point(-1, -1);
		patternFinish = false;
		mDrawTouchExtension = false;
		mDisplayingPracticeResult = false;
		mPracticeFailureMode = new FailureHighlight();
		mPracticeSuccessMode = new SuccessHighlight();
		mPracticeFinishMode = new FirstHighlight();
		mHandler = new Handler();
		mVibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
		patternPx = new CenterIterator();
		//
		mEdgePaint = new Paint();
		mEdgePaint.setColor(EDGE_COLOR);
		mEdgePaint.setStrokeCap(Paint.Cap.ROUND);
		mEdgePaint.setFlags(Paint.ANTI_ALIAS_FLAG);
	}

	// called whenever either the actual drawn length or the nodewise length
	// changes
	/** 构造绘制的点信息 **/
	private void buildDrawables() {
		// 页面绘制的点数 [3*3]
		mNodeDrawables = new NodeDrawable[mLengthNodes][mLengthNodes];
		// 每个Cell[3*3格: 9个Cell]的长度
		mCellLength = mLengthPx / mLengthNodes;
		// Node 直径
		float nodeDiameter = ((float) mCellLength) * CELL_NODE_RATIO;
		// 点的填充宽度
		mEdgePaint.setStrokeWidth(nodeDiameter * NODE_EDGE_RATIO);
		// Touch 绘制的宽度
		mTouchThreshold = (int) (nodeDiameter / 2);
		int cellHalf = mCellLength / 2;

		long buildStart = System.currentTimeMillis();
		for (int y = 0; y < mLengthNodes; y++) {
			for (int x = 0; x < mLengthNodes; x++) {
				// if just building the drawables is taking too long, bail!
				if (System.currentTimeMillis() - buildStart >= BUILD_TIMEOUT_MILLIS) {
					EmergencyExit.clearAndBail(getContext());
				}
				Point center = new Point(x * mCellLength + cellHalf, y * mCellLength + cellHalf);
				mNodeDrawables[x][y] = new NodeDrawable(nodeDiameter, center);
			}
		}

		// re-highlight nodes if not in practice
		if (!mPracticeMode) {
			loadPattern(mCurrentPattern, mHighlightMode);
		}
	}

	private void clearPattern(List<Point> pattern) {
		for (Point e : pattern) {
			mNodeDrawables[e.x][e.y].setNodeState(NodeDrawable.STATE_UNSELECTED);
		}
	}

	/**
	 * 加载到图案对象池
	 * 
	 * @param pattern
	 *            匹配Point点的List集合
	 * @param highlightMode
	 *            高亮模式
	 */
	private void loadPattern(List<Point> pattern, HighlightMode highlightMode) {
		for (int ii = 0; ii < pattern.size(); ii++) {
			Point e = pattern.get(ii);
			NodeDrawable node = mNodeDrawables[e.x][e.y];
			int state = highlightMode.select(node, ii, pattern.size(), e.x, e.y, mLengthNodes);
			node.setNodeState(state); // rolls off the tongue
			// if another node follows, then tell the current node which way
			// to point
			if (ii < pattern.size() - 1) {
				Point f = pattern.get(ii + 1);
				Point centerE = mNodeDrawables[e.x][e.y].getCenter();
				Point centerF = mNodeDrawables[f.x][f.y].getCenter();

				mNodeDrawables[e.x][e.y].setExitAngle((float) Math.atan2(centerE.y - centerF.y, centerE.x - centerF.x));
			}
		}
	}

	// only works properly with practice mode due to highlighting, should
	// probably be generalized and used to replace the bulk of loadPattern()
	private void appendPattern(List<Point> pattern, Point node) {
		NodeDrawable nodeDraw = mNodeDrawables[node.x][node.y];
		nodeDraw.setNodeState(NodeDrawable.STATE_SELECTED);
		if (pattern.size() > 0) {
			Point tailNode = pattern.get(pattern.size() - 1);
			NodeDrawable tailDraw = mNodeDrawables[tailNode.x][tailNode.y];

			Point tailCenter = tailDraw.getCenter();
			Point nodeCenter = nodeDraw.getCenter();

			tailDraw.setExitAngle((float) Math.atan2(tailCenter.y - nodeCenter.y, tailCenter.x - nodeCenter.x));
		}
		pattern.add(node);
		if (pattern.size() == 1 && patternListener != null) {
			patternListener.testPracticeStart();
		}
	}

	/** 校验图案匹配结果 **/
	private void testPracticePattern() {
		mDisplayingPracticeResult = true;
		HighlightMode mode = mPracticeFailureMode;
		// List 匹配
		if (patternListener != null) {
			boolean patterned = patternListener.testPatternPractice(mPracticePattern);
			if (patterned && patternFinish) {
				mode = mPracticeFinishMode;
				clearPattern(mCurrentPattern);
				mCurrentPattern = mPracticePattern;

			} else if (patterned) {
				mode = mPracticeSuccessMode;
			}
		} else if (mPracticePattern.equals(mCurrentPattern)) {
			mode = mPracticeSuccessMode;
		}
		loadPattern(mPracticePattern, mode);
		// clear the result display after a delay
		// 延时清空匹配结果
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				// 正在显示匹配结果
				if (mDisplayingPracticeResult) {
					resetPractice(); // 重置绘画图像缓冲
					invalidate(); // 重绘
				}
			}
		}, PRACTICE_RESULT_DISPLAY_MILLIS);
	}

	private void resetPractice() {
		clearPattern(mPracticePattern);
		mPracticePattern.clear(); // 绘制匹配List
		mPracticePool.clear(); // 绘制池
		mDisplayingPracticeResult = false; // 显示绘制结果[绘制高亮]
	}

	public void resetPattern() {
		clearPattern(mCurrentPattern);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// draw pattern edges first
		Point edgeStart, edgeEnd;
		List<Point> pattern = mCurrentPattern;
		if (mPracticeMode) {
			pattern = mPracticePattern;
		}

		patternPx.initIterator(pattern.iterator());
		if (patternPx.hasNext()) {
			edgeStart = patternPx.next(); // 开始边界
			while (patternPx.hasNext()) {
				edgeEnd = patternPx.next(); // 结束边界
				canvas.drawLine(edgeStart.x, edgeStart.y, edgeEnd.x, edgeEnd.y, mEdgePaint);
				edgeStart = edgeEnd;
			}
			if (mDrawTouchExtension) {
				// 绘制Touch 扩展线
				canvas.drawLine(edgeStart.x, edgeStart.y, mTouchPoint.x, mTouchPoint.y, mEdgePaint);
			}
		}

		// then draw nodes
		for (int y = 0; y < mLengthNodes; y++) {
			for (int x = 0; x < mLengthNodes; x++) {
				mNodeDrawables[x][y].draw(canvas);
			}
		}
	}

	/** Touch Event **/
	@Override
	@SuppressLint("ClickableViewAccessibility")
	public boolean onTouchEvent(MotionEvent event) {
		// Practice Mode 绘制模式,false return super:不执行事件
		if (!mPracticeMode) {
			return super.onTouchEvent(event);
		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// 显示绘制结果
			if (mDisplayingPracticeResult) {
				resetPractice();
			}
			mDrawTouchExtension = true;
		case MotionEvent.ACTION_MOVE:
			float x = event.getX(),
			y = event.getY();
			mTouchPoint.x = (int) x;
			mTouchPoint.y = (int) y;
			mTouchCell.x = (int) x / mCellLength;
			mTouchCell.y = (int) y / mCellLength;
			if (mTouchCell.x < 0 || mTouchCell.x >= mLengthNodes || mTouchCell.y < 0 || mTouchCell.y >= mLengthNodes) {
				break;
			}
			// 最近Node的中心
			Point nearestCenter = mNodeDrawables[mTouchCell.x][mTouchCell.y].getCenter();
			int dist = (int) Math.sqrt(Math.pow(x - nearestCenter.x, 2) + Math.pow(y - nearestCenter.y, 2));
			if (dist < mTouchThreshold && !mPracticePool.contains(mTouchCell)) {
				if (mTactileFeedback) {
					// 震动
					mVibrator.vibrate(TACTILE_FEEDBACK_DURATION);
				}
				// 添加新的点Point
				Point newPoint = new Point(mTouchCell);
				appendPattern(mPracticePattern, newPoint); // 添加到图像绘制列表
				mPracticePool.add(newPoint); // 添加到图像匹配池
			}
			break;
		case MotionEvent.ACTION_UP:
			mDrawTouchExtension = false;
			testPracticePattern();
			break;
		default:
			return super.onTouchEvent(event);
		}
		invalidate(); // invalidate View call the onDraw Method
		return true;
	}

	// expand to be as large as the smallest dictated size, or to the default
	// length if both dimensions are unspecified
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int length = 0;
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int wMode = MeasureSpec.getMode(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		int hMode = MeasureSpec.getMode(heightMeasureSpec);

		if (wMode == MeasureSpec.UNSPECIFIED && hMode == MeasureSpec.UNSPECIFIED) {
			length = DEFAULT_LENGTH_PX;
			setMeasuredDimension(length, length);
		} else if (wMode == MeasureSpec.UNSPECIFIED) {
			length = height;
		} else if (hMode == MeasureSpec.UNSPECIFIED) {
			length = width;
		} else {
			length = Math.min(width, height);
		}

		setMeasuredDimension(length, length);
	}

	// update draw values dependent on view size so it doesn't have to happen
	// in every onDraw()
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mLengthPx = Math.min(w, h);
		buildDrawables();
		if (!mPracticeMode) {
			loadPattern(mCurrentPattern, mHighlightMode);
		}
	}

	//
	// Accessors / Mutators
	//

	public void setPattern(List<Point> pattern) {
		clearPattern(mCurrentPattern);
		loadPattern(pattern, mHighlightMode);
		mCurrentPattern = pattern;
	}

	public List<Point> getPattern() {
		return mCurrentPattern;
	}

	public void setGridLength(int length) {
		mLengthNodes = length;
		mCurrentPattern = Collections.emptyList(); // 清空当前图像模式缓冲
		buildDrawables();
	}

	public int getGridLength() {
		return mLengthNodes;
	}

	public void setHighlightMode(HighlightMode mode) {
		setHighlightMode(mode, mPracticeMode);
	}

	public void setHighlightMode(HighlightMode mode, boolean suppressRepaint) {
		mHighlightMode = mode;
		if (!suppressRepaint) {
			loadPattern(mCurrentPattern, mHighlightMode);
		}
	}

	public HighlightMode getHighlightMode() {
		return mHighlightMode;
	}

	/**
	 * 执行类型模式
	 * 
	 * @param mode
	 *            true:可绘制,false:不可绘制
	 **/
	public void setPracticeMode(boolean mode) {
		mDisplayingPracticeResult = false;
		mPracticeMode = mode;
		if (mode) {
			// 可绘制[清空原图]
			mPracticePattern = new ArrayList<Point>();
			mPracticePool = new HashSet<Point>();
			clearPattern(mCurrentPattern);
		} else {
			// 不可绘制
			clearPattern(mPracticePattern);
			loadPattern(mCurrentPattern, mHighlightMode);
		}
	}

	public boolean getPracticeMode() {
		return mPracticeMode;
	}

	/** 设置接触点时震动 **/
	public void setTactileFeedbackEnabled(boolean enabled) {
		mTactileFeedback = enabled;
	}

	public boolean getTactileFeedbackEnabled() {
		return mTactileFeedback;
	}

	public void tactileExecute(long time) {
		if (mTactileFeedback) {
			mVibrator.vibrate(time);
		}
	}

	public void setPatternFinish(boolean finish) {
		patternFinish = finish;

		mPracticeMode = false;
		mDisplayingPracticeResult = false;
	}

	/***************************** Inner Class *****************************/
	/** 中心迭代器[Node 点迭代] **/
	private class CenterIterator implements Iterator<Point> {
		private Iterator<Point> nodeIterator;

		public CenterIterator() {}

		public void initIterator(Iterator<Point> nodeIterator) {
			this.nodeIterator = nodeIterator;
		}

		@Override
		public boolean hasNext() {
			return nodeIterator.hasNext();
		}

		@Override
		public Point next() {
			Point node = nodeIterator.next();
			return mNodeDrawables[node.x][node.y].getCenter();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	// Interface for choosing what state to put a node in based on its position
	// in the pattern, allowing for things like highlighting the first node etc.
	public interface HighlightMode {
		// Node 的高亮状态
		int select(NodeDrawable node, int patternIndex, int patternLength, int nodeX, int nodeY, int gridLength);
	}

	/** 图像匹配回调 **/
	public interface PatternListener {
		public boolean testPatternPractice(List<Point> mCurrentPattern);

		public void testPracticeStart();
	}

	/** 无高亮 模式 */
	public static class NoHighlight implements HighlightMode {
		@Override
		public int select(NodeDrawable node, int patternIndex, int patternLength, int nodeX, int nodeY, int gridLength) {
			return NodeDrawable.STATE_SELECTED;
		}
	}

	/** 第一个Node 高亮 **/
	public static class FirstHighlight implements HighlightMode {
		@Override
		public int select(NodeDrawable node, int patternIndex, int patternLength, int nodeX, int nodeY, int gridLength) {
			if (patternIndex == 0) {
				return NodeDrawable.STATE_HIGHLIGHTED;
			}
			return NodeDrawable.STATE_SELECTED;
		}
	}

	/** 彩虹高亮 模式 */
	public static class RainbowHighlight implements HighlightMode {
		@Override
		public int select(NodeDrawable node, int patternIndex, int patternLength, int nodeX, int nodeY, int gridLength) {
			float wheelPosition = ((float) patternIndex / (float) patternLength) * 360.0f;
			int color = Color.HSVToColor(new float[] { wheelPosition, 1.0f, 1.0f });
			node.setCustomColor(color);
			return NodeDrawable.STATE_CUSTOM;
		}
	}

	/** 匹配失败高亮 模式 */
	public static class FailureHighlight implements HighlightMode {
		@Override
		public int select(NodeDrawable node, int patternIndex, int patternLength, int nodeX, int nodeY, int gridLength) {
			return NodeDrawable.STATE_INCORRECT;
		}
	}

	/** 匹配成功高亮 模式 */
	public static class SuccessHighlight implements HighlightMode {
		@Override
		public int select(NodeDrawable node, int patternIndex, int patternLength, int nodeX, int nodeY, int gridLength) {
			return NodeDrawable.STATE_CORRECT;
		}
	}

	public void setPatternListener(PatternListener patternListener) {
		this.patternListener = patternListener;
	}
}
