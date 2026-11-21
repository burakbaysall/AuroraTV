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
    private static final int MUTED = 0xffb9bec9;
    private LinearLayout hero;
    private TextView heroTitle, heroDescription, heroEyebrow, clock;
    private int accent = 0xff9bc8ff;
    private final int[] cardColors = {0xff222831,0xff222831,0xff222831,0xff222831,0xff222831,0xff222831};
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
        heroEyebrow.setText(selected ? "SEÇİLİ UYGULAMA" : "HOŞ GELDİN");
        heroTitle.setText(selected ? appName : "Tüm uygulamaların. Tek ekranda.");
        heroDescription.setText(selected ? "Açmak için OK tuşuna bas" : "Kumandanla keşfet, seç ve izle.");
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
                new int[]{0xff1d2534,0xff11151e,0xff090b10,0xff131622});
        scroll.setBackground(background);
        LinearLayout root = vertical();
        root.setPadding(dp(48),dp(18),dp(48),dp(22));
        scroll.addView(root);
        setContentView(scroll);

        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);
        root.addView(header,new LinearLayout.LayoutParams(-1,dp(34)));
        TextView logo = label("✦  AURORA TV",19,WHITE,true);
        logo.setLetterSpacing(.14f);
        header.addView(logo,new LinearLayout.LayoutParams(0,-2,1));
        TextView topHint = label("ANA EKRAN",11,0xff92a8c8,true);
        topHint.setLetterSpacing(.14f);
        header.addView(topHint);
        clock = label("",17,WHITE,true);
        LinearLayout.LayoutParams timeParams = new LinearLayout.LayoutParams(-2,-2);
        timeParams.leftMargin=dp(28);
        header.addView(clock,timeParams);
        updateClock();

        hero = vertical();
        hero.setPadding(dp(27),dp(19),dp(27),dp(16));
        GradientDrawable heroBackground = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{0xff29384c,0xff202c3d,0xff1c2231});
        heroBackground.setCornerRadius(dp(18));
        hero.setBackground(heroBackground);
        LinearLayout.LayoutParams heroParams = new LinearLayout.LayoutParams(-1,dp(124));
        heroParams.topMargin=dp(14);
        root.addView(hero,heroParams);
        heroEyebrow = label("HOŞ GELDİN",10,0xffb8c9e2,true);
        heroEyebrow.setLetterSpacing(.18f);
        hero.addView(heroEyebrow);
        heroTitle = label("",25,WHITE,true);
        LinearLayout.LayoutParams headlineParams = new LinearLayout.LayoutParams(-1,-2);
        headlineParams.topMargin=dp(8);
        hero.addView(heroTitle,headlineParams);
        heroDescription = label("",12,0xffd2def0,false);
        LinearLayout.LayoutParams descParams = new LinearLayout.LayoutParams(-1,-2);
        descParams.topMargin=dp(7);
        hero.addView(heroDescription,descParams);
        updateHero("",false);

        LinearLayout sectionHeader = new LinearLayout(this);
        sectionHeader.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams sectionParams = new LinearLayout.LayoutParams(-1,dp(48));
        sectionParams.topMargin=dp(5);
        root.addView(sectionHeader,sectionParams);
        TextView sectionTitle = label("Uygulamalar",20,WHITE,true);
        sectionHeader.addView(sectionTitle,new LinearLayout.LayoutParams(0,-2,1));
        TextView sectionHint = label("YÖN TUŞLARIYLA GEZİN",10,MUTED,true);
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
            card.setBackground(shape(cardColors[index%cardColors.length],19));
            GridLayout.LayoutParams cardParams = new GridLayout.LayoutParams(
                    GridLayout.spec(index/5),GridLayout.spec(index%5));
            cardParams.width=dp(165);
            cardParams.height=dp(140);
            cardParams.setMargins(dp(0),dp(0),dp(12),dp(13));
            grid.addView(card,cardParams);

            LinearLayout content = vertical();
            content.setGravity(Gravity.CENTER);
            content.setPadding(dp(10),dp(10),dp(10),dp(10));
            card.addView(content,new FrameLayout.LayoutParams(-1,-1));
            ImageView icon = new ImageView(this);
            try {
                Drawable drawable=app.loadIcon(pm);
                icon.setImageDrawable(drawable);
            } catch (Exception ignored) {
                icon.setImageResource(android.R.drawable.sym_def_app_icon);
            }
            icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
            content.addView(icon,new LinearLayout.LayoutParams(dp(79),dp(79)));
            TextView appLabel=label(name,12,WHITE,true);
            appLabel.setGravity(Gravity.CENTER);
            appLabel.setSingleLine(true);
            appLabel.setEllipsize(android.text.TextUtils.TruncateAt.END);
            LinearLayout.LayoutParams appLabelParams = new LinearLayout.LayoutParams(-1,-2);
            appLabelParams.topMargin=dp(8);
            content.addView(appLabel,appLabelParams);

            final int cardColor=cardColors[index%cardColors.length];
            card.setOnFocusChangeListener((v,focused)->{
                v.animate().scaleX(focused?1.09f:1f).scaleY(focused?1.09f:1f)
                        .setDuration(170).start();
                v.setElevation(focused?dp(22):dp(2));
                GradientDrawable tile=shape(focused?0xff39485e:cardColor,19);
                if(focused) tile.setStroke(dp(2),0xfff5f7ff);
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
            TextView empty=label("Android TV uygulaması bulunamadı.",16,MUTED,false);
            root.addView(empty);
        } else {
            focusableCards.get(0).requestFocus();
        }
        TextView footer=label("AURORA TV 3.0   •   OK: AÇ   •   GERİ: DÖN",10,0xff8597b5,false);
        footer.setLetterSpacing(.08f);
        LinearLayout.LayoutParams footerParams=new LinearLayout.LayoutParams(-1,-2);
        footerParams.topMargin=dp(8);
        root.addView(footer,footerParams);
    }
    private int lighten(int color) {
        int r=Math.min(255,Color.red(color)+26);
        int g=Math.min(255,Color.green(color)+29);
        int b=Math.min(255,Color.blue(color)+34);
        return Color.rgb(r,g,b);
    }
}