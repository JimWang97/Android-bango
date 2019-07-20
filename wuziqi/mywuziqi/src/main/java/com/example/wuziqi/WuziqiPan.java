package com.example.wuziqi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * 玩家对战
 * Created by Administrator on 2018/10/6.
 */

public class WuziqiPan extends View {
    private int mPanelWidth;
    private float mLineHeight;
    private int MAX_LINE=15;//总共15行
    private Paint mpaint=new Paint();
    private Bitmap mWhitePiece;
    private Bitmap mBlackPiece;
    public boolean mIsGameOver;
    private boolean mIsWhiteWinner;
    private float ratio=3*1.0f/4;
    private boolean mIsWhite = true;  //轮到白棋下
    private ArrayList<Point> mWhiteArray=new ArrayList<>();
    private ArrayList<Point> mBlackArray=new ArrayList<>();
    public boolean whiteWin;
    public boolean blackWin;


    public WuziqiPan(Context context, AttributeSet attrs) {
        super(context, attrs);
        //setBackgroundColor(0x44ff0000);
        init();
    }

    private void init() {
        mpaint.setColor(0x88000000);
        mpaint.setAntiAlias(true);
        mpaint.setDither(true);
        mpaint.setStyle(Paint.Style.STROKE);
        mWhitePiece=BitmapFactory.decodeResource(getResources(),R.drawable.white);
        mBlackPiece=BitmapFactory.decodeResource(getResources(),R.drawable.black);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {//测量获取长宽
        int widthsize=MeasureSpec.getSize(widthMeasureSpec);
        int widthmode=MeasureSpec.getMode(widthMeasureSpec);
        int heightsize=MeasureSpec.getSize(heightMeasureSpec);
        int heightmode=MeasureSpec.getMode(heightMeasureSpec);
        int width=Math.min(widthsize,heightsize);
        if (widthmode==MeasureSpec.UNSPECIFIED){//防止width和height中有一个是0
            width=heightsize;
        }else if (heightmode==MeasureSpec.UNSPECIFIED){
            width=widthsize;
        }
        setMeasuredDimension(width,width);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {//改变原始图片尺寸
        super.onSizeChanged(w, h, oldw, oldh);
        mPanelWidth=w;
        mLineHeight=mPanelWidth*1.0f/MAX_LINE;
        int piecewidth=(int)(mLineHeight*ratio);
        mWhitePiece=Bitmap.createScaledBitmap(mWhitePiece,piecewidth,piecewidth,false);
        mBlackPiece=Bitmap.createScaledBitmap(mBlackPiece,piecewidth,piecewidth,false);
    }

    @Override
    protected void onDraw(Canvas canvas) {//开始游戏
        super.onDraw(canvas);
        drawBoard(canvas);
        drawPieces(canvas);
        checkGameOver();
    }

    private void checkGameOver(){
        whiteWin= checkFiveInLine(mWhiteArray);
        blackWin= checkFiveInLine(mBlackArray);
        if (whiteWin||blackWin){
            mIsGameOver=true;
            mIsWhiteWinner=whiteWin;
            String text= mIsWhiteWinner?"白棋胜利":"黑棋胜利";
            Toast.makeText(getContext(),text,Toast.LENGTH_SHORT).show();
        }
    }

    private void drawPieces(Canvas canvas) {//画棋子
        for (int i = 0, n=mWhiteArray.size(); i < n; i++) {//白子
            Point whitePoint = mWhiteArray.get(i);
            //drawBitmap是将图片的右下角为坐标
            canvas.drawBitmap(mWhitePiece, ((whitePoint.x + (1 - ratio) / 2) * mLineHeight), (whitePoint.y + (1 - ratio) / 2) * mLineHeight, null);
        }

        for (int i = 0, n=mBlackArray.size(); i < n; i++) {//黑子
            Point blackPoint = mBlackArray.get(i);
            //drawBitmap是将图片的右下角为坐标
            canvas.drawBitmap(mBlackPiece, ((blackPoint.x + (1 - ratio) / 2) * mLineHeight), (blackPoint.y + (1 - ratio) / 2) * mLineHeight, null);
        }
    }


    private void drawBoard(Canvas canvas) {//绘制棋盘
        int w=mPanelWidth;
        float lineHeight=mLineHeight;
        for (int i=0;i<MAX_LINE;i++){
            int startX=(int)(lineHeight/2);
            int endX=(int)(w-lineHeight/2);
            int y=(int)((0.5+i)*lineHeight);
            canvas.drawLine(startX,y,endX,y,mpaint);//横坐标
            canvas.drawLine(y,startX,y,endX,mpaint);//纵坐标
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(mIsGameOver) return false;
        int action=event.getAction();
        if (action==MotionEvent.ACTION_UP){
            int x=(int)event.getX();
            int y=(int)event.getY();
            Point p=getPoint(x,y);
            if (mWhiteArray.contains(p)||mBlackArray.contains(p)){
                return false;
            }
            if (mIsWhite){
                mWhiteArray.add(p);
            }else {
                mBlackArray.add(p);
            }
            invalidate();
            mIsWhite=!mIsWhite;
        }

    return true;
    }

    public Point getPoint(int x, int y) {//将数值大小转换成整数坐标点
        return new Point((int) (x / mLineHeight), (int) (y / mLineHeight));
    }

    public boolean checkFiveInLine(ArrayList<Point> points){
        for(Point p : points){
            int x=p.x;
            int y=p.y;
            boolean win= checkHorizontal(x,y,points);
            if (win) return true;
            win= checkVertical(x,y,points);
            if (win) return true;
            win= checkRight(x,y,points);
            if (win) return true;
            win= checkLift(x,y,points);
            if (win) return true;
        }
        return false;
    }

    public boolean checkHorizontal(int x, int y, ArrayList<Point> points){//判断一行的五子情况
        int count=1;
        for (int i=1;i<5;i++){//横向左边
            if (points.contains(new Point(x-i,y))){
                count++;
            }
            else{
                break;
            }
        }
        if (count==5){
            return true;
        }
        for (int i=1;i<5;i++){//横向右边
            if (points.contains(new Point(x+i,y))){
                count++;
            }
            else{
                break;
            }
        }
        if (count==5){
                return true;
        }
        return false;
    }

    public boolean checkVertical(int x, int y, ArrayList<Point> points){//判断一列的五子情况
        int count=1;
        for (int i=1;i<5;i++){//竖向上边
            if (points.contains(new Point(x,y-i))){
                count++;
            }
            else{
                break;
            }
        }
        if (count==5){
            return true;
        }
        for (int i=1;i<5;i++){//竖向下边
            if (points.contains(new Point(x,y+i))){
                count++;
            }
            else{
                break;
            }
        }
        if (count==5){
            return true;
        }
        return false;
    }

    public boolean checkLift(int x, int y, ArrayList<Point> points){//判断斜向的五子情况
        int count=1;
        for (int i=1;i<5;i++){//右上
            if (points.contains(new Point(x+i,y-i))){
                count++;
            }
            else{
                break;
            }
        }
        if (count==5){
            return true;
        }
        for (int i=1;i<5;i++){//左下
            if (points.contains(new Point(x-i,y+i))){
                count++;
            }
            else{
                break;
            }
        }
        if (count==5){
            return true;
        }
        return false;
    }

    public boolean checkRight(int x, int y, ArrayList<Point> points){//判断斜向的五子情况
        int count=1;
        for (int i=1;i<5;i++){//左下
            if (points.contains(new Point(x+i,y+i))){
                count++;
            }
            else{
                break;
            }
        }
        if (count==5){
            return true;
        }
        for (int i=1;i<5;i++){//右上
            if (points.contains(new Point(x-i,y-i))){
                count++;
            }
            else{
                break;
            }
        }
        if (count==5){
            return true;
        }
        return false;
    }

    public void start(){
        mWhiteArray.clear();
        mBlackArray.clear();
        mIsGameOver=false;
        mIsWhiteWinner=false;
        mIsWhite=true;
        invalidate();
    }


    private static final String INSTANCE = "instance";
    private static final String INSTANCE_GAMEOVER = "instance_gameover";
    private static final String INSTANCE_WHITEARRAY = "instance_whitearray";
    private static final String INSTANCE_BLACKARRAY = "instance_blackarray";


    @Override
    protected Parcelable onSaveInstanceState() {//保存
        Bundle bundle = new Bundle();
        //保存系统默认状态
        bundle.putParcelable(INSTANCE, super.onSaveInstanceState());
        //保存是否游戏结束的值
        bundle.putBoolean(INSTANCE_GAMEOVER, mIsGameOver);
        //保存白棋的子数
        bundle.putParcelableArrayList(INSTANCE_WHITEARRAY, mWhiteArray);
        //保存黑棋的子数
        bundle.putParcelableArrayList(INSTANCE_BLACKARRAY, mBlackArray);
        return bundle;
    }


    @Override
    protected void onRestoreInstanceState(Parcelable state) {//读出

        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mIsGameOver = bundle.getBoolean(INSTANCE_GAMEOVER);
            mWhiteArray = bundle.getParcelableArrayList(INSTANCE_WHITEARRAY);
            mBlackArray = bundle.getParcelableArrayList(INSTANCE_BLACKARRAY);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE));
            return;
        }
        super.onRestoreInstanceState(state);
    }

}
