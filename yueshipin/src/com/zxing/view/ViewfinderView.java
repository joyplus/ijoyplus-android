/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zxing.view;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import com.google.zxing.ResultPoint;
import com.joyplus.R;
import com.zxing.camera.CameraManager;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder
 * rectangle and partial transparency outside it, as well as the laser scanner
 * animation and result points. �Զ����View������ʱ�м���ʾ��
 */
public final class ViewfinderView extends View {

	private static final int[] SCANNER_ALPHA = { 0, 64, 128, 192, 255, 192,
			128, 64 };
	private static final long ANIMATION_DELAY = 100L;
	private static final int OPAQUE = 0xFF;

	private final Paint paint;
	private Bitmap resultBitmap;
	private final int maskColor;
	private final int resultColor;
	private final int frameColor;
	private final int laserColor;
	private final int resultPointColor;
	private int scannerAlpha;
	private Collection<ResultPoint> possibleResultPoints;
	private Collection<ResultPoint> lastPossibleResultPoints;
	private int bmpDTY = 0;
	private int maxDTY = 0;
	Rect frame;
	int width;

	// This constructor is used when the class is built from an XML resource.
	public ViewfinderView(Context context, AttributeSet attrs) {
		super(context, attrs);

		// Initialize these once for performance rather than calling them every
		// time in onDraw().
		paint = new Paint();
		Resources resources = getResources();
		maskColor = resources.getColor(R.color.viewfinder_mask);
		resultColor = resources.getColor(R.color.result_view);
		frameColor = resources.getColor(R.color.viewfinder_frame);
		laserColor = resources.getColor(R.color.viewfinder_laser);
		resultPointColor = resources.getColor(R.color.possible_result_points);
		scannerAlpha = 0;
		possibleResultPoints = new HashSet<ResultPoint>(5);
	}

	@Override
	public void onDraw(Canvas canvas) {
		resultBitmap = (Bitmap) BitmapFactory.decodeResource(getResources(),
				R.drawable.saomiao);
		frame = CameraManager.get().getFramingRect();
		if (frame == null) {
			return;
		}
		width = canvas.getWidth();

		if (width <= 480) {
			if (bmpDTY == 0) {
				bmpDTY = frame.top + 20;
				maxDTY = frame.top +200;
			}
			if (resultBitmap != null) {
				paint.setAlpha(OPAQUE);
				canvas.drawBitmap(resultBitmap, frame.left + 70, bmpDTY, paint);
			}
		} else if (width > 500) {
			if (bmpDTY == 0) {
				bmpDTY = frame.top - 80;
				maxDTY = frame.top + 220;
			}
			if (resultBitmap != null) {
				paint.setAlpha(OPAQUE);
				canvas.drawBitmap(resultBitmap, frame.left + 5, bmpDTY, paint);
			}
		}
		handler.postDelayed(runnable, 500);
	}

	public void drawViewfinder() {
		resultBitmap = (Bitmap) BitmapFactory.decodeResource(getResources(),
				R.drawable.saomiao);
		invalidate();
	}

	/**
	 * Draw a bitmap with the result points highlighted instead of the live
	 * scanning display.
	 * 
	 * @param barcode
	 *            An image of the decoded barcode.
	 */
	public void drawResultBitmap(Bitmap barcode) {
		resultBitmap = barcode;
		invalidate();
	}

	public void addPossibleResultPoint(ResultPoint point) {
		possibleResultPoints.add(point);
	}

	Handler handler = new Handler();
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			if (bmpDTY != 0 && bmpDTY < maxDTY) {
				bmpDTY += 10;
			} else if (bmpDTY == maxDTY) {
				bmpDTY = 0;
			}
			ViewfinderView.this.postInvalidate();
		}
	};
}
