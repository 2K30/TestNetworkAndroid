package com._2K30.testnetworkadndroid.common;

/**
 * // Created by JeffMeJones@gmail.com
 */
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;


public class GifRunCommon implements Runnable, Callback {

    public Bitmap bmb;
    public GIFDecodeCommon decode;
    public int ind;
    public int gifCount;
    public SurfaceHolder mSurfaceHolder ;
    boolean surfaceExists;
    public void LoadGiff(SurfaceView v, android.content.Context theTHIS, int R_drawable)
    {
//InputStream Raw= context.getResources().openRawResource(R.drawable.image001);
        mSurfaceHolder = v.getHolder();
        mSurfaceHolder.addCallback(this);
        decode = new GIFDecodeCommon();
        decode.read(theTHIS.getResources().openRawResource(R_drawable));
        ind = 0;
// decode.
        gifCount = decode.getFrameCount();
        bmb = decode.getFrame(0);
        surfaceExists=true;
        Thread t = new Thread(this);
        t.start();
    }

    public void run()
    {
        while (surfaceExists) {
            try {
                Canvas rCanvas = mSurfaceHolder.lockCanvas();
                Paint clearPaint = new Paint ();
                rCanvas.drawRect (0, 0, 800, 500, clearPaint);
                clearPaint.setXfermode (new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                rCanvas.drawColor (Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                Bitmap b = Bitmap.createBitmap(bmb,0,0,500,500);
                rCanvas.drawBitmap(b, ((rCanvas.getWidth() - decode.width) / 2),
                        ((rCanvas.getHeight() - decode.height) / 2), new Paint());
                mSurfaceHolder.unlockCanvasAndPost(rCanvas);
                bmb = decode.next();
                Thread.sleep(50);
            } catch (Exception ex) {

            }
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height)
    {
    }

    public void surfaceCreated(SurfaceHolder holder)
    {
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        surfaceExists=false;
    }
}
