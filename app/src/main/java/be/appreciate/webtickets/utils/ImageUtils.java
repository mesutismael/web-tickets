package be.appreciate.webtickets.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 * Created by Inneke De Clippel on 29/03/2016.
 */
public class ImageUtils
{
    public static void setDrawableLeft(TextView textView, @DrawableRes int vectorDrawableRes, Context context)
    {
        VectorDrawableCompat vector = VectorDrawableCompat.create(context.getResources(), vectorDrawableRes, context.getTheme());
        textView.setCompoundDrawablesWithIntrinsicBounds(vector, null, null, null);
    }

    public static void tintDrawables(TextView textView, @ColorRes int colorRes)
    {
        if(textView != null)
        {
            Drawable[] drawables = textView.getCompoundDrawables();
            textView.setCompoundDrawablesWithIntrinsicBounds(
                    ImageUtils.tintIcon(textView.getContext(), drawables[0], colorRes),
                    ImageUtils.tintIcon(textView.getContext(), drawables[1], colorRes),
                    ImageUtils.tintIcon(textView.getContext(), drawables[2], colorRes),
                    ImageUtils.tintIcon(textView.getContext(), drawables[3], colorRes)
            );
        }
    }

    public static Drawable tintIcon(Context context, Drawable drawable, @ColorRes int colorRes)
    {
        if(drawable != null)
        {
            int color = ContextCompat.getColor(context, colorRes);
            Drawable.ConstantState state = drawable.getConstantState();
            drawable = DrawableCompat.wrap(state == null ? drawable : state.newDrawable()).mutate();
            DrawableCompat.setTint(drawable, color);
        }

        return drawable;
    }

    public static void loadImage(ImageView imageView, String imageUrl, @DrawableRes int placeholderRes)
    {
        if(!TextUtils.isEmpty(imageUrl))
        {
            Glide.with(imageView.getContext())
                    .load(imageUrl)
                    .placeholder(placeholderRes)
                    .error(placeholderRes)
                    .into(imageView);
        }
        else
        {
            imageView.setImageResource(placeholderRes);
        }
    }
}
