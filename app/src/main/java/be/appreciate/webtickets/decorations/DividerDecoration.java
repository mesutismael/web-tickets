package be.appreciate.webtickets.decorations;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import be.appreciate.webtickets.R;

/**
 * Created by Inneke De Clippel on 29/03/2016.
 */
public class DividerDecoration extends RecyclerView.ItemDecoration
{
    private Context context;
    private float height;
    private Paint paint;
    private float paddingLeft;

    public DividerDecoration(Context context)
    {
        this.context = context;
        this.setHeightRes(R.dimen.general_divider);

        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.setColorRes(R.color.general_divider);
    }

    public void setColor(int color)
    {
        this.paint.setColor(color);
    }

    public void setColorRes(@ColorRes int colorRes)
    {
        this.paint.setColor(ContextCompat.getColor(this.context, colorRes));
    }

    public void setHeight(float height)
    {
        this.height = height;
    }

    public void setHeightRes(@DimenRes int heightRes)
    {
        this.height = this.context.getResources().getDimension(heightRes);
    }

    public void setPaddingLeft(float paddingLeft)
    {
        this.paddingLeft = paddingLeft;
    }

    public void setPaddingLeftRes(@DimenRes int paddingLeftRes)
    {
        this.paddingLeft = this.context.getResources().getDimension(paddingLeftRes);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)
    {
        super.getItemOffsets(outRect, view, parent, state);

        if (parent.getChildLayoutPosition(view) < 1)
            return;

        if (parent.getLayoutManager() instanceof LinearLayoutManager)
        {
            switch (((LinearLayoutManager) parent.getLayoutManager()).getOrientation())
            {
                case LinearLayoutManager.VERTICAL:
                    outRect.top = (int) this.height;
                    break;

                case LinearLayoutManager.HORIZONTAL:
                    outRect.left = (int) this.height;
                    break;
            }
        }
        else if (parent.getLayoutManager() instanceof StaggeredGridLayoutManager)
        {
            switch (((StaggeredGridLayoutManager) parent.getLayoutManager()).getOrientation())
            {
                case StaggeredGridLayoutManager.VERTICAL:
                    outRect.top = (int) this.height;
                    break;

                case StaggeredGridLayoutManager.HORIZONTAL:
                    outRect.left = (int) this.height;
                    break;
            }
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state)
    {
        if (parent.getLayoutManager() instanceof LinearLayoutManager)
        {
            LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
            int childCount = parent.getChildCount();
            float left = parent.getPaddingLeft() + this.paddingLeft;
            float top = parent.getPaddingTop();
            float right = parent.getWidth() - parent.getPaddingRight();
            float bottom = parent.getHeight() - parent.getPaddingBottom();

            switch (layoutManager.getOrientation())
            {
                case LinearLayoutManager.VERTICAL:
                    for (int i = 0; i < childCount; i++)
                    {
                        View child = parent.getChildAt(i);

                        if (parent.getChildLayoutPosition(child) < 1)
                            continue;

                        float decorationTop = layoutManager.getDecoratedTop(child);
                        float decorationBottom = decorationTop + this.height;

                        c.drawRect(left, decorationTop, right, decorationBottom, this.paint);
                    }
                    break;

                case LinearLayoutManager.HORIZONTAL:
                    for (int i = 0; i < childCount; i++)
                    {
                        View child = parent.getChildAt(i);

                        if (parent.getChildLayoutPosition(child) < 1)
                            continue;

                        float decorationLeft = layoutManager.getDecoratedLeft(child);
                        float decorationRight = decorationLeft + this.height;

                        c.drawRect(decorationLeft, top, decorationRight, bottom, this.paint);
                    }
                    break;
            }
        }
        else if(parent.getLayoutManager() instanceof StaggeredGridLayoutManager)
        {
            StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) parent.getLayoutManager();
            int childCount = parent.getChildCount();

            switch (layoutManager.getOrientation())
            {
                case StaggeredGridLayoutManager.VERTICAL:
                    for (int i = 0; i < childCount; i++)
                    {
                        View child = parent.getChildAt(i);

                        if (parent.getChildLayoutPosition(child) < layoutManager.getSpanCount())
                            continue;

                        float decorationTop = layoutManager.getDecoratedTop(child);
                        float decorationBottom = decorationTop + this.height;
                        float decorationLeft = layoutManager.getDecoratedLeft(child) + this.paddingLeft;
                        float decorationRight = layoutManager.getDecoratedRight(child);

                        c.drawRect(decorationLeft, decorationTop, decorationRight, decorationBottom, this.paint);
                    }
                    break;

                case StaggeredGridLayoutManager.HORIZONTAL:
                    for (int i = 0; i < childCount; i++)
                    {
                        View child = parent.getChildAt(i);

                        if (parent.getChildLayoutPosition(child) < layoutManager.getSpanCount())
                            continue;

                        float decorationTop = layoutManager.getDecoratedTop(child) + this.paddingLeft;
                        float decorationBottom = layoutManager.getDecoratedBottom(child);
                        float decorationLeft = layoutManager.getDecoratedLeft(child);
                        float decorationRight = decorationLeft + this.height;

                        c.drawRect(decorationLeft, decorationTop, decorationRight, decorationBottom, this.paint);
                    }
                    break;
            }
        }
    }
}
