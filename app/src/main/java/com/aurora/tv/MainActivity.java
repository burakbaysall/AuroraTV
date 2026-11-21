package com.aurora.tv;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {
    private static final int WHITE = 0xffffffff;
    private static final int MUTED = 0xffa8b5cc;
    private LinearLayout hero;
    private TextView heroTitle, heroDescription, heroEyebrow, clock;
    private int accent = 0xff9bc8ff;
    private final int[] cardColors = {0xff233e61,0xff45315c,0xff254c4b,0xff5c393a,0xff363f66,0xff4b422b};
    private final ArrayList<View> focusableCards = new ArrayList<>();

    private int dp(float n) { return (int)(n * getResources().getDisplayMetrics().density + .5f); }
    private GradientDrawable shape(int color, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color); d.setCornerRadius(dp(radius)); return d;
    }
    private TextView label(String text, int sp, int color, boolean bold) {
        TextView v = new TextView(this);
        v.setText(text); v.setTextSize(sp); v.setTextColor(color);
        v.setIncludeFontPadding(false);
        if (bold) v.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        return v;
    }
    private LinearLayout vertical() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        return l;
    }
    private void updateClock() {
        if (clock != null) clock.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
    }
    @Override public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); render(); }
    @Override protected void onResume() { super.onResume(); updateClock(); }

    private void updateHero(String appName, boolean selected) {
        if (heroTitle == null) return;
        heroEyebrow.setText(selected ? "READY TO OPEN" : "WELCOME TO YOUR SPACE");
        heroTitle.setText(selected ? appName : "Your entertainment.\nBeautifully organized.");
        heroDescription.setText(selected ? "Press OK to open " + appName : "Discover everything you love, all in one place.");
    }

    private void render() {
        getWindow().setStatusBarColor(0xff080e1a);
        getWindow().setNavigationBarColor(0xff080e1a);
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setClipToPadding(false);
        scroll.setVerticalScrollBarEnabled(false);
        GradientDrawable background = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{0xff152c48,0xff101525,0xff090c16,0xff1a132b});
        scroll.setBackground(background);
        LinearLayout root = vertical();
        root.setPadding(dp(52),dp(28),dp(52),dp(40));
        scroll.addView(root);
        setContentView(scroll);

        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);
        root.addView(header,new LinearLayout.LayoutParams(-1,dp(42)));
        TextView logo = label("✦  AURORA",22,WHITE,true);
        logo.setLetterSpacing(.14f);
        header.addView(logo,new LinearLayout.LayoutParams(0,-2,1));
        TextView topHint = label("HOME  /  APPS",11,0xff92a8c8,true);
        topHint.setLetterSpacing(.14f);
        header.addView(topHint);
        clock = label("",17,WHITE,true);
        LinearLayout.LayoutParams timeParams = new LinearLayout.LayoutParams(-2,-2);
        timeParams.leftMargin=dp(28);
        header.addView(clock,timeParams);
        updateClock();

        hero = vertical();
        hero.setPadding(dp(32),dp(26),dp(32),dp(26));
        GradientDrawable heroBackground = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{0xff304f7a,0xff263451,0xff38294f});
        heroBackground.setCornerRadius(dp(22));
        hero.setBackground(heroBackground);
        LinearLayout.LayoutParams heroParams = new LinearLayout.LayoutParams(-1,dp(218));
        heroParams.topMargin=dp(24);
        root.addView(hero,heroParams);
        heroEyebrow = label("WELCOME TO YOUR SPACE",11,0xffbdd9ff,true);
        heroEyebrow.setLetterSpacing(.18f);
        hero.addView(heroEyebrow);
        heroTitle = label("",34,WHITE,true);
        LinearLayout.LayoutParams headlineParams = new LinearLayout.LayoutParams(-1,-2);
        headlineParams.topMargin=dp(16);
        hero.addView(heroTitle,headlineParams);
        heroDescription = label("",14,0xffd2def0,false);
        LinearLayout.LayoutParams descParams = new LinearLayout.LayoutParams(-1,-2);
        descParams.topMargin=dp(12);
        hero.addView(heroDescription,descParams);
        updateHero("",false);

        LinearLayout sectionHeader = new LinearLayout(this);
        sectionHeader.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams sectionParams = new LinearLayout.LayoutParams(-1,dp(64));
        sectionParams.topMargin=dp(10);
        root.addView(sectionHeader,sectionParams);
        TextView sectionTitle = label("Your Apps",22,WHITE,true);
        sectionHeader.addView(sectionTitle,new LinearLayout.LayoutParams(0,-2,1));
        TextView sectionHint = label("USE YOUR REMOTE TO EXPLORE",10,MUTED,true);
        sectionHint.setLetterSpacing(.1f);
        sectionHeader.addView(sectionHint);

        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(5);
        grid.setUseDefaultMargins(false);
        root.addView(grid,new LinearLayout.LayoutParams(-1,-2));

        PackageManager pm = getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER);
        List<ResolveInfo> apps = new ArrayList<>(pm.queryIntentActivities(intent,0));
        Collections.sort(apps, new Comparator<ResolveInfo>() {
            @Override public int compare(ResolveInfo a, ResolveInfo b) {
                return String.CASE_INSENSITIVE_ORDER.compare(
                        a.loadLabel(pm).toString(),b.loadLabel(pm).toString());
            }
        });
        int index=0;
        for (ResolveInfo app : apps) {
            if (app.activityInfo == null || getPackageName().equals(app.activityInfo.packageName)) continue;
            final String name=app.loadLabel(pm).toString();
            final String pkg=app.activityInfo.packageName;
            final String activityName=app.activityInfo.name;
            FrameLayout card = new FrameLayout(this);
            card.setFocusable(true);
            card.setClickable(true);
            card.setClipChildren(false);
            card.setClipToPadding(false);
            card.setBackground(shape(cardColors[index%cardColors.length],18));
            GridLayout.LayoutParams cardParams = new GridLayout.LayoutParams(
                    GridLayout.spec(index/5),GridLayout.spec(index%5));
            cardParams.width=dp(155);
            cardParams.height=dp(123);
            cardParams.setMargins(dp(0),dp(0),dp(13),dp(14));
            grid.addView(card,cardParams);

            LinearLayout content = vertical();
            content.setGravity(Gravity.CENTER);
            content.setPadding(dp(8),dp(12),dp(8),dp(10));
            card.addView(content,new FrameLayout.LayoutParams(-1,-1));
            ImageView icon = new ImageView(this);
            try {
                Drawable drawable=app.loadIcon(pm);
                icon.setImageDrawable(drawable);
            } catch (Exception ignored) {
                icon.setImageResource(android.R.drawable.sym_def_app_icon);
            }
            icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
            content.addView(icon,new LinearLayout.LayoutParams(dp(55),dp(55)));
            TextView appLabel=label(name,13,WHITE,true);
            appLabel.setGravity(Gravity.CENTER);
            appLabel.setSingleLine(true);
            appLabel.setEllipsize(android.text.TextUtils.TruncateAt.END);
            LinearLayout.LayoutParams appLabelParams = new LinearLayout.LayoutParams(-1,-2);
            appLabelParams.topMargin=dp(12);
            content.addView(appLabel,appLabelParams);

            final int cardColor=cardColors[index%cardColors.length];
            card.setOnFocusChangeListener((v,focused)->{
                v.animate().scaleX(focused?1.075f:1f).scaleY(focused?1.075f:1f)
                        .setDuration(170).start();
                v.setElevation(focused?dp(18):dp(2));
                GradientDrawable tile=shape(focused?lighten(cardColor):cardColor,18);
                if(focused) tile.setStroke(dp(2),0xffd6e8ff);
                v.setBackground(tile);
                if(focused) updateHero(name,true);
            });
            card.setOnClickListener(v->{
                try {
                    Intent launch=pm.getLeanbackLaunchIntentForPackage(pkg);
                    if(launch==null) launch=pm.getLaunchIntentForPackage(pkg);
                    if(launch==null) {
                        launch=new Intent(Intent.ACTION_MAIN);
                        launch.setClassName(pkg,activityName);
                        launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    }
                    startActivity(launch);
                } catch (Exception ex) {
                    android.widget.Toast.makeText(this,"Unable to open " + name,android.widget.Toast.LENGTH_SHORT).show();
                }
            });
            focusableCards.add(card);
            index++;
        }
        if(index==0){
            TextView empty=label("No Android TV apps found.",16,MUTED,false);
            root.addView(empty);
        } else {
            focusableCards.get(0).requestFocus();
        }
        TextView footer=label("AURORA TV   •   PRESS OK TO OPEN AN APP   •   PRESS BACK TO RETURN",10,0xff8597b5,false);
        footer.setLetterSpacing(.08f);
        LinearLayout.LayoutParams footerParams=new LinearLayout.LayoutParams(-1,-2);
        footerParams.topMargin=dp(20);
        root.addView(footer,footerParams);
    }
    private int lighten(int color) {
        int r=Math.min(255,Color.red(color)+26);
        int g=Math.min(255,Color.green(color)+29);
        int b=Math.min(255,Color.blue(color)+34);
        return Color.rgb(r,g,b);
    }
}