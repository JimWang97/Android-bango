package com.example.wuziqi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * 高级版人机
 * Created by Administrator on 2018/10/6.
 */

public class WuziqiPanrenji2 extends View {
    public int mPanelWidth;
    public float mLineHeight;
    public int MAX_LINE=15;//总共15行
    public Paint mpaint=new Paint();
    public Bitmap mWhitePiece;
    public Bitmap mBlackPiece;
    public boolean mIsGameOver;
    public boolean mIsWhiteWinner;
    public float ratio=3*1.0f/4;
    public boolean mIsWhite = true;  //轮到白棋下
    public ArrayList<Point> mWhiteArray=new ArrayList<>();
    public ArrayList<Point> mBlackArray=new ArrayList<>();
    public boolean whiteWin;
    public boolean blackWin;
    int QiPan[][]=new int[15][15];
    int whiteQiPan[][]=new int[15][15];
    int blackQiPan[][]=new int[15][15];
    private static int[][] position = {

            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },

            { 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0 },

            { 0, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 0 },

            { 0, 1, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 1, 0 },

            { 0, 1, 2, 3, 4, 4, 4, 4, 4, 4, 4, 3, 2, 1, 0 },

            { 0, 1, 2, 3, 4, 5, 5, 5, 5, 5, 4, 3, 2, 1, 0 },

            { 0, 1, 2, 3, 4, 5, 6, 6, 6, 5, 4, 3, 2, 1, 0 },

            { 0, 1, 2, 3, 4, 5, 6, 7, 6, 5, 4, 3, 2, 1, 0 },

            { 0, 1, 2, 3, 4, 5, 6, 6, 6, 5, 4, 3, 2, 1, 0 },

            { 0, 1, 2, 3, 4, 5, 5, 5, 5, 5, 4, 3, 2, 1, 0 },

            { 0, 1, 2, 3, 4, 4, 4, 4, 4, 4, 4, 3, 2, 1, 0 },

            { 0, 1, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 1, 0 },

            { 0, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 0 },

            { 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0 },

            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 } };

    public WuziqiPanrenji2(Context context, AttributeSet attrs) {
        super(context, attrs);
        //setBackgroundColor(0x44ff0000);
        init();
        for(int i=0;i<15;i++){
            for(int j=0;j<15;j++){
                QiPan[i][j]=0;
                whiteQiPan[i][j]=0;
                blackQiPan[i][j]=0;
            }
        }
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
            if (mIsWhite){ // 默认人为白棋 白棋下
                mWhiteArray.add(p);
                forceLayout();
                QiPan[p.x][p.y]=1;
                mIsWhite=!mIsWhite;
            }
            if(checkFiveInLine(mWhiteArray)) {
                checkGameOver();
            }
            if(!mIsWhite&&mBlackArray.size()<mWhiteArray.size()&&!mIsGameOver){//新增加判断
                Point pB=AIGo(QiPan);//AI判断怎么下黑棋
                mBlackArray.add(pB);
                QiPan[pB.x][pB.y]=2;
                invalidate();
                mIsWhite=!mIsWhite;
            }
        }

        return true;
    }

    private Point AIGo(int[][] qiPan) {//博弈树 极大极小值搜索
        Point down=new Point();
        Point tempdown=new Point();
        int[][] Q=new int[15][15];
        int[][] score=new int[15][15];
        int best=0;
        int x,y;
        int alpha,beta;
        alpha=-100000;
        beta=100000;
        int depth=1;
        for(int i=0;i<15;i++) {
            for(int j=0;j<15;j++) {
                if((mBlackArray.contains(new Point(i-1,j))||
                        mBlackArray.contains(new Point(i+1,j))||
                        mBlackArray.contains(new Point(i-1,j-1))||
                        mBlackArray.contains(new Point(i-1,j+1))||
                        mBlackArray.contains(new Point(i+1,j-1))||
                        mBlackArray.contains(new Point(i+1,j+1))||
                        mBlackArray.contains(new Point(i,j-1))||
                        mBlackArray.contains(new Point(i,j+1))||
                        mWhiteArray.contains(new Point(i-1,j))||
                        mWhiteArray.contains(new Point(i+1,j))||
                        mWhiteArray.contains(new Point(i-1,j-1))||
                        mWhiteArray.contains(new Point(i-1,j+1))||
                        mWhiteArray.contains(new Point(i+1,j-1))||
                        mWhiteArray.contains(new Point(i+1,j+1))||
                        mWhiteArray.contains(new Point(i,j-1))||
                        mWhiteArray.contains(new Point(i,j+1)))&&qiPan[i][j]==0) {
                    tempdown.x = i;
                    tempdown.y = j;
                    mBlackArray.add(tempdown);
                    if(checkFiveInLine(mBlackArray)) {
                        score[i][j] = 1000000000;
                        down=tempdown;
                        return tempdown;
                    }
                    else {
                        mBlackArray.remove(mBlackArray.size() - 1);
                        qiPan[i][j] = 2;
                        score[i][j] = MaxMinAlphaBeta(qiPan, 1, depth, tempdown, alpha, beta, mBlackArray, mWhiteArray);
                        score[i][j] = score[i][j] + position[i][j];
                        qiPan[i][j] = 0;
                    }
                }
                else{
                    score[i][j]=-1000000;
                }
            }
        }
        down=MaxPoint(score);
        return down;
    }

    private int MaxMinAlphaBeta(int[][] QiPan, int whiteOrBlack, int depth, Point opPos, int alpha, int beta, ArrayList<Point> mBlackArray,ArrayList<Point> mWhiteArray){
        int bestValue=0, curValue=0;  // bestValue是最好的分数， curValue是试探下子后，该次下子的分数
        Point p=new Point();

        if (checkFiveInLine(mWhiteArray))  // 玩家赢
        {
            return -100000;
        }
        else if(checkFiveInLine(mBlackArray))   // 电脑赢
        {
            return 100000;
        }

        if (depth == 0)   //深度为0，估值返回
        {
            bestValue = AI(QiPan,1);
        }
        else
        {
            // 下面开始是利用了剪枝思想的极大极小过程
            if (whiteOrBlack == 2)  // 此节点为电脑黑子，取极大值
            {
                // 下面开始对各个可以下的子进行评分
                for (int i = 0; i <= 14; i++)
                {
                    for (int j = 0; j <= 14; j++)
                    {
                        if (QiPan[i][j]==0)  // 如果可以下子
                        {
                            if (alpha >= beta)  // alpha剪枝
                            {
                                return alpha;
                            }
                            QiPan[i][j] = whiteOrBlack; // 试探下子
                            p.x=i;
                            p.y=j;
                            mBlackArray.add(p);
                            curValue = MaxMinAlphaBeta(QiPan, 1, depth - 1, p, alpha, beta,mBlackArray,mWhiteArray);
                            QiPan[i][j] = 0;  // 撤消下子
                            mBlackArray.remove(mBlackArray.size()-1);
                            if (curValue > alpha)
                            {
                                alpha = curValue; //子节点的最大值记录到alpha中
                            }
                        }
                    }
                }
                bestValue = alpha;
            }
            else if (whiteOrBlack == 1)  // 此节点为玩家白子，取极小值
            {
                for (int i = 0; i <= 14; i++)
                {
                    for (int j = 0; j <= 14; j++)
                    {
                        if ((mBlackArray.contains(new Point(i-1,j))||
                                mBlackArray.contains(new Point(i+1,j))||
                                mBlackArray.contains(new Point(i-1,j-1))||
                                mBlackArray.contains(new Point(i-1,j+1))||
                                mBlackArray.contains(new Point(i+1,j-1))||
                                mBlackArray.contains(new Point(i+1,j+1))||
                                mBlackArray.contains(new Point(i,j-1))||
                                mBlackArray.contains(new Point(i,j+1))||
                                mWhiteArray.contains(new Point(i-1,j))||
                                mWhiteArray.contains(new Point(i+1,j))||
                                mWhiteArray.contains(new Point(i-1,j-1))||
                                mWhiteArray.contains(new Point(i-1,j+1))||
                                mWhiteArray.contains(new Point(i+1,j-1))||
                                mWhiteArray.contains(new Point(i+1,j+1))||
                                mWhiteArray.contains(new Point(i,j-1))||
                                mWhiteArray.contains(new Point(i,j+1)))&&QiPan[i][j]==0)  // 如果可以下子
                        {
                            if (alpha >= beta)  // beta剪枝
                            {
                                return beta;
                            }
                            QiPan[i][j] = whiteOrBlack; // 试探下子
                            p.x=i;
                            p.y=j;
                            mWhiteArray.add(p);
                            curValue = MaxMinAlphaBeta(QiPan, 2, depth - 1, p, alpha, beta,mBlackArray,mWhiteArray);
                            QiPan[i][j] = 0;  // 撤消下子
                            mWhiteArray.remove(mWhiteArray.size()-1);
                            if (curValue < beta)
                            {
                                beta = curValue; //子节点的最小值记录到beta中
                            }
                        }
                    }
                } // end else if (whiteOrBlack == 1)
                bestValue = beta;
            }
        }  // end else : (depth > 0)

        return bestValue;

    }

    private ArrayList<Point> generate_point(int[][] qiPan) {
        ArrayList<Point> P=new ArrayList<>();
        Point p=new Point();
        for(int i=0;i<15;i++){
            for(int j=0;j<15;j++){
                if(qiPan[i][j]==0){
                    p.x=i;
                    p.y=j;
                    P.add(p);
                }

            }
        }
        return P;
    }

    public int Max(int [][]a){
        int max = a[0][0];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[i].length; j++) {
                if (max < a[i][j]) {
                    max = a[i][j];//算出最大值
                }
            }
        }
        return max;
    }
    public int Min(int [][]a){
        int min = a[0][0];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[i].length; j++) {
                if (min > a[i][j]) {
                    min = a[i][j];//算出最小值
                }
            }
        }
        return min;
    }
    public Point MaxPoint(int [][]a){
        int x=0;
        int y=0;
        int max=a[0][0];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[i].length; j++) {
                if (max < a[i][j]) {
                    max = a[i][j];//算出最大值
                    x=i;
                    y=j;
                }
            }
        }
        Point p=new Point();
        p.x=x;
        p.y=y;
        return p;
    }
    public Point MinPoint(int [][]a){
        int x=0;
        int y=0;
        int min=a[0][0];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[i].length; j++) {
                if (min > a[i][j]) {
                    min = a[i][j];//算出最小值
                    x=i;
                    y=j;
                }
            }
        }
        Point p=new Point();
        p.x=x;
        p.y=y;
        return p;
    }

    public int AI(int[][] QiPan,int flag) {//AI判断

        int dx[] = {1, 0, 1, 1};
        int dy[] = {0, 1, 1, -1};
        int tempx = 1, tempy = 1;
        int score=0;
        int ans = 0;
        int N = 15;
        for(int i=0;i<15;i++){
            for(int j=0;j<15;j++){
                whiteQiPan[i][j]=0;
                blackQiPan[i][j]=0;
            }
        }
        for (int x = 0; x < N; x++) {
            for (int y = 0; y < N; y++) {
                if (QiPan[x][y] == 2)
                    continue;

                int num[][] = new int[2][100];

                for (int i = 0; i < 4; i++) {
                    int sum = 1;
                    int flag1 = 0, flag2 = 0;

                    int tx = x + dx[i];
                    int ty = y + dy[i];
                    while (tx >= 0 && tx < N
                            && ty >= 0 && ty < N
                            && QiPan[tx][ty] == 1) {
                        tx += dx[i];
                        ty += dy[i];
                        ++sum;
                    }

                    if (tx >= 0 && tx < N
                            && ty >= 0 && ty < N
                            && QiPan[tx][ty] == 0)
                        flag1 = 1;

                    tx = x - dx[i];
                    ty = y - dy[i];
                    while (tx >= 0 && tx < N
                            && ty >= 0 && ty < N
                            && QiPan[tx][ty] == 1) {
                        tx -= dx[i];
                        ty -= dy[i];
                        ++sum;
                    }

                    if (tx >= 0 && tx < N
                            && ty >= 0 && ty < N
                            && QiPan[tx][ty] == 0)
                        flag2 = 1;

                    if (flag1 + flag2 > 0)
                        ++num[flag1 + flag2 - 1][sum];
                }

                //成5
                if (num[0][5] + num[1][5] > 0) {
                    whiteQiPan[x][y] = 100000;
                    ans = Math.max(ans, 100000);
                    if (ans == 100000) {
                        tempx = x;
                        tempy = y;
                    }
                }
                //活4 | 双死四 | 死4活3
                else if (num[1][4] > 0
                        || num[0][4] > 1
                        || (num[0][4] > 0 && num[1][3] > 0)) {
                    whiteQiPan[x][y] = 10000;
                    ans = Math.max(ans, 10000);
                    if (ans == 10000) {
                        tempx = x;
                        tempy = y;
                    }
                }
                //双活3
                else if (num[1][3] > 1) {
                    whiteQiPan[x][y] = 5000;
                    ans = Math.max(ans, 5000);
                    if (ans == 5000) {
                        tempx = x;
                        tempy = y;
                    }
                }
                //死3活3
                else if (num[1][3] > 0 && num[0][3] > 0) {
                    whiteQiPan[x][y] = 1000;
                    whiteQiPan[x][y] = 1000;
                    ans = Math.max(ans, 1000);
                    if (ans == 1000) {
                        tempx = x;
                        tempy = y;
                    }
                }
                //死4
                else if (num[0][4] > 0) {
                    whiteQiPan[x][y] = 500;
                    whiteQiPan[x][y] = 500;
                    ans = Math.max(ans, 500);
                    if (ans == 500) {
                        tempx = x;
                        tempy = y;
                    }
                }
                //单活3
                else if (num[1][3] > 0) {
                    whiteQiPan[x][y] = 200;
                    ans = Math.max(ans, 200);
                    if (ans == 200) {
                        tempx = x;
                        tempy = y;
                    }
                }
                //双活2
                else if (num[1][2] > 1) {
                    whiteQiPan[x][y] = 100;
                    ans = Math.max(ans, 100);
                    if (ans == 100) {
                        tempx = x;
                        tempy = y;
                    }
                }
                //死3
                else if (num[0][3] > 0) {
                    whiteQiPan[x][y] = 50;
                    ans = Math.max(ans, 50);
                    if (ans == 50) {
                        tempx = x;
                        tempy = y;
                    }
                }
                //双活2
                else if (num[1][2] > 1) {
                    whiteQiPan[x][y] = 10;
                    ans = Math.max(ans, 10);
                    if (ans == 10) {
                        tempx = x;
                        tempy = y;
                    }
                }
                //单活2
                else if (num[1][2] > 0) {
                    whiteQiPan[x][y] = 5;
                    ans = Math.max(ans, 5);
                    if (ans == 5) {
                        tempx = x;
                        tempy = y;
                    }
                }
                //死2
                else if (num[0][2] > 0) {
                    whiteQiPan[x][y] = 3;
                    ans = Math.max(ans, 1);
                    if (ans == 3) {
                        tempx = x;
                        tempy = y;
                    }
                }

            }
        }

        //进攻
        int ans1 = 0;
        for (int x = 0; x < N; x++) {
            for (int y = 0; y < N; y++) {
                if (QiPan[x][y] == 1)
                    continue;

                int num[][] = new int[2][100];

                for (int i = 0; i < 4; i++) {
                    int sum = 1;
                    int flag1 = 0, flag2 = 0;

                    int tx = x + dx[i];
                    int ty = y + dy[i];
                    while (tx >= 0 && tx < N
                            && ty >= 0 && ty < N
                            && QiPan[tx][ty] == 2) {
                        tx += dx[i];
                        ty += dy[i];
                        ++sum;
                    }

                    if (tx >= 0 && tx < N
                            && ty >= 0 && ty < N
                            && QiPan[tx][ty] == 0)
                        flag1 = 1;

                    tx = x - dx[i];
                    ty = y - dy[i];
                    while (tx >= 0 && tx < N
                            && ty >= 0 && ty < N
                            && QiPan[tx][ty] == 2) {
                        tx -= dx[i];
                        ty -= dy[i];
                        ++sum;
                    }

                    if (tx >= 0 && tx < N
                            && ty >= 0 && ty < N
                            && QiPan[tx][ty] == 0)
                        flag2 = 1;

                    if (flag1 + flag2 > 0)
                        ++num[flag1 + flag2 - 1][sum];
                }

                //成5
                if (num[0][5] + num[1][5] > 0) {
                    blackQiPan[x][y] = 100000;
                    ans1 = Math.max(ans1, 100000);
                    if (ans1 == 100000) {
                        tempx = x;
                        tempy = y;
                    }
                }
                //活4 | 双死四 | 死4活3
                else if (num[1][4] > 0
                        || num[0][4] > 1
                        || (num[0][4] > 0 && num[1][3] > 0)) {
                    blackQiPan[x][y] = 10000;
                    ans1 = Math.max(ans1, 10000);
                    if (ans1 == 10000) {
                        tempx = x;
                        tempy = y;
                    }
                }
                //双活3
                else if (num[1][3] > 1) {
                    blackQiPan[x][y] = 5000;
                    ans1 = Math.max(ans1, 5000);
                    if (ans1 == 5000) {
                        tempx = x;
                        tempy = y;
                    }
                }
                //死3活3
                else if (num[1][3] > 0 && num[0][3] > 0) {
                    blackQiPan[x][y] = 1000;
                    ans1 = Math.max(ans1, 1000);
                    if (ans1 == 1000) {
                        tempx = x;
                        tempy = y;
                    }
                }
                //死4
                else if (num[0][4] > 0) {
                    blackQiPan[x][y] = 500;
                    ans1 = Math.max(ans1, 500);
                    if (ans1 == 500) {
                        tempx = x;
                        tempy = y;
                    }
                }
                //单活3
                else if (num[1][3] > 0) {
                    blackQiPan[x][y] = 200;
                    ans1 = Math.max(ans1, 200);
                    if (ans1 == 200) {
                        tempx = x;
                        tempy = y;
                    }
                }
                //双活2
                else if (num[1][2] > 1) {
                    blackQiPan[x][y] = 100;
                    ans1 = Math.max(ans1, 100);
                    if (ans1 == 100) {
                        tempx = x;
                        tempy = y;
                    }
                }
                //死3
                else if (num[0][3] > 0) {
                    blackQiPan[x][y] = 50;
                    ans1 = Math.max(ans1, 50);
                    if (ans1 == 50) {
                        tempx = x;
                        tempy = y;
                    }
                }
                //双活2
                else if (num[1][2] > 1) {
                    blackQiPan[x][y] = 10;
                    ans1 = Math.max(ans1, 10);
                    if (ans1 == 10) {
                        tempx = x;
                        tempy = y;
                    }
                }
                //单活2
                else if (num[1][2] > 0) {
                    blackQiPan[x][y] = 5;
                    ans1 = Math.max(ans1, 5);
                    if (ans1 == 5) {
                        tempx = x;
                        tempy = y;
                    }
                }
                //死2
                else if (num[0][2] > 0) {
                    blackQiPan[x][y] = 3;
                    ans1 = Math.max(ans1, 1);
                    if (ans1 == 3) {
                        tempx = x;
                        tempy = y;
                    }
                }

            }
        }
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                score = blackQiPan[i][j] - whiteQiPan[i][j]+score;
            }
        }

        /*
        ArrayList<Point> down = new ArrayList<Point>();
        Point p = new Point();
        int k = 1;
        if (flag == 1){
            while (k <= 1) {
                ans1 = Max(blackQiPan);
                if (QiPan[MaxPoint(blackQiPan).x][MaxPoint(blackQiPan).y] == 0) {
                    p.x = MaxPoint(blackQiPan).x;
                    p.y = MaxPoint(blackQiPan).y;
                    down.add(p);
                    k = k + 1;
                    break;
                } else
                    blackQiPan[MaxPoint(blackQiPan).x][MaxPoint(blackQiPan).y] = -1;
            }
    }
        else if(flag==2){
            while (k <= 1) {
                ans1 = Min(blackQiPan);
                if (QiPan[MinPoint(blackQiPan).x][MinPoint(blackQiPan).y] == 0) {
                    p.x = MinPoint(blackQiPan).x;
                    p.y = MinPoint(blackQiPan).y;
                    down.add(p);
                    k = k + 1;
                    break;
                } else
                    blackQiPan[MinPoint(blackQiPan).x][MinPoint(blackQiPan).y] = 100000;
            }
        }

            return down;
            */
        return score;
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
        blackWin=false;
        mIsWhite=true;
        for(int i=0;i<15;i++){
            for(int j=0;j<15;j++){
                QiPan[i][j]=0;
                whiteQiPan[i][j]=0;
                blackQiPan[i][j]=0;
            }
        }
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
