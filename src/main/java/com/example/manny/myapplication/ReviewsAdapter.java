package com.example.manny.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.MyViewHolder> {
    public List<Details.Result.Reviews> reviewsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, text, date;
        public ImageView img;
        public String review_url;
        public RatingBar ratingBar;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.author_name);
            text = (TextView) view.findViewById(R.id.review_text);
            date = (TextView) view.findViewById(R.id.review_date);
            img = (ImageView) view.findViewById(R.id.author_img);
            ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(review_url));
                    v.getContext().startActivity(i);
                }
            });
        }
    }

    public ReviewsAdapter(List<Details.Result.Reviews> reviewsList) {
        this.reviewsList = reviewsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return reviewsList.size();
    }

    @Override
    public void onBindViewHolder(final ReviewsAdapter.MyViewHolder holder, int position) {
        final Details.Result.Reviews review = reviewsList.get(position);
        holder.name.setText(review.author_name);
        holder.text.setText(review.text);
        //long epoch = Long.parseLong(Integer.toString(review.time * 1000));
        Date dateObj = new Date(review.time * 1000);
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        holder.date.setText(df.format(dateObj));
        Picasso.get().load(review.profile_photo_url).transform(new CircleTransform()).resize(100, 100).into(holder.img);
        holder.ratingBar.setRating(review.rating);
        holder.review_url = review.author_url;

        //holder.img.setImage(result.getImg());
    }

    public class CircleTransform implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());

            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                source.recycle();
            }

            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap,
                    BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);

            squaredBitmap.recycle();
            return bitmap;
        }

        @Override
        public String key() {
            return "circle";
        }
    }
}
