package com.aurora.tv;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.text.TextUtils;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {
    private final Handler handler = new Handler();
    private final ArrayList<ResolveInfo> apps = new ArrayList<>();
    private final ArrayList<View> appTiles = new ArrayList<>();
    private final int WHITE=Color.WHITE, MUTED=0xffc5ccda;
    private FrameLayout stage;
    private TextView timeLabel, dateLabel, hintLabel;
    private boolean showingApps=false, daytime=false;
    private int selected=0;
    private final Runnable clockTick=new Runnable(){
        @Override public void run(){
            if(timeLabel!=null) timeLabel.setText(new SimpleDateFormat("H:mm",Locale.getDefault()).format(new Date()));
            if(dateLabel!=null) dateLabel.setText(new SimpleDateFormat("EEEE, d MMMM",new Locale("tr","TR")).format(new Date()));
            handler.postDelayed(this,15000);
        }
    };
    private int d(float x){return (int)(getResources().getDisplayMetrics().density*x+.5f);}
    private GradientDrawable round(int color,int radius){
        GradientDrawable g=new GradientDrawable();g.setColor(color);g.setCornerRadius(d(radius));return g;
    }
    private GradientDrawable gradient(int[] colors,int radius){
        GradientDrawable g=new GradientDrawable(GradientDrawable.Orientation.TL_BR,colors);
        g.setCornerRadius(d(radius));return g;
    }
    private TextView txt(String value,int size,int color,boolean bold){
        TextView t=new TextView(this);t.setText(value);t.setTextSize(size);t.setTextColor(color);
        t.setIncludeFontPadding(false);
        if(bold)t.setTypeface(Typeface.create("sans-serif",Typeface.BOLD));
        return t;
    }
    private LinearLayout column(){LinearLayout v=new LinearLayout(this);v.setOrientation(1);return v;}
    private void loadApps(){
        PackageManager pm=getPackageManager();
        Intent q=new Intent(Intent.ACTION_MAIN);q.addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER);
        apps.clear();
        for(ResolveInfo r:pm.queryIntentActivities(q,0)){
            if(r.activityInfo!=null && !getPackageName().equals(r.activityInfo.packageName))apps.add(r);
        }
        Collections.sort(apps,(a,b)->String.CASE_INSENSITIVE_ORDER.compare(
            a.loadLabel(pm).toString(),b.loadLabel(pm).toString()));
    }
    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        getWindow().getDecorView().setSystemUiVisibility(5894|1024|512);
        loadApps();showClock();handler.post(clockTick);
    }
    @Override protected void onDestroy(){handler.removeCallbacks(clockTick);super.onDestroy();}
    private void setupStage(){
        stage=new FrameLayout(this);
        stage.setFocusableInTouchMode(true);
        stage.setBackground(new GradientDrawable(GradientDrawable.Orientation.TL_BR,
            daytime?new int[]{0xff607c91,0xff334d6b,0xff171d31}:new int[]{0xff101928,0xff172239,0xff070b15}));
        setContentView(stage);
    }
    private void topBar(){
        LinearLayout top=new LinearLayout(this);top.setGravity(Gravity.CENTER_VERTICAL);
        FrameLayout.LayoutParams lp=new FrameLayout.LayoutParams(-1,d(46),Gravity.TOP);
        lp.setMargins(d(52),d(24),d(52),0);stage.addView(top,lp);
        TextView brand=txt("✦  AURORA",17,WHITE,true);brand.setLetterSpacing(.13f);
        top.addView(brand,new LinearLayout.LayoutParams(0,-2,1));
        addPill(top,"☀  Gündüz / Gece",()->{daytime=!daytime; if(showingApps)showApps();else showClock();});
        addPill(top,"⚙  Ayarlar",()->{
            try{startActivity(new Intent("android.settings.SETTINGS"));}
            catch(Exception e){android.widget.Toast.makeText(this,"Ayarlar açılamadı",0).show();}
        });
        addPill(top,showingApps?"◷  Saat":"▦  Uygulamalar",()->{
            if(showingApps)showClock();else showApps();
        });
    }
    private void addPill(LinearLayout parent,String name,Runnable action){
        TextView p=txt(name,12,WHITE,false);p.setGravity(Gravity.CENTER);
        p.setPadding(d(16),d(9),d(16),d(9));p.setBackground(round(0x66434d61,24));
        p.setFocusable(true);p.setOnClickListener(v->action.run());
        p.setOnFocusChangeListener((v,f)->{
            v.animate().scaleX(f?1.06f:1).scaleY(f?1.06f:1).setDuration(150).start();
            v.setBackground(round(f?0xff64748c:0x66434d61,24));
        });
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-2,-2);lp.leftMargin=d(10);
        parent.addView(p,lp);
    }
    private void showClock(){
        showingApps=false;appTiles.clear();setupStage();topBar();
        LinearLayout center=column();center.setGravity(Gravity.CENTER);
        FrameLayout.LayoutParams cp=new FrameLayout.LayoutParams(-1,-2,Gravity.CENTER);
        cp.bottomMargin=d(20);stage.addView(center,cp);
        dateLabel=txt("",18,0xffd1d7e1,false);dateLabel.setGravity(Gravity.CENTER);
        center.addView(dateLabel);
        timeLabel=txt("",112,0xfff1f2fa,true);timeLabel.setGravity(Gravity.CENTER);
        timeLabel.setTypeface(Typeface.create("sans-serif-condensed",Typeface.BOLD));
        timeLabel.setLetterSpacing(-.055f);
        LinearLayout.LayoutParams tp=new LinearLayout.LayoutParams(-1,-2);tp.topMargin=d(2);
        center.addView(timeLabel,tp);
        dateLabel.setText(new SimpleDateFormat("EEEE, d MMMM",new Locale("tr","TR")).format(new Date()));
        timeLabel.setText(new SimpleDateFormat("H:mm",Locale.getDefault()).format(new Date()));
        LinearLayout bottom=column();bottom.setGravity(Gravity.CENTER);
        FrameLayout.LayoutParams bp=new FrameLayout.LayoutParams(-1,-2,Gravity.BOTTOM);
        bp.bottomMargin=d(28);stage.addView(bottom,bp);
        TextView section=txt("UYGULAMALAR",11,0xffbdc9da,true);
        section.setLetterSpacing(.16f);section.setGravity(Gravity.CENTER);bottom.addView(section);
        LinearLayout dock=new LinearLayout(this);dock.setGravity(Gravity.CENTER);
        dock.setPadding(d(13),d(10),d(13),d(10));
        dock.setBackground(round(0x88404a5d,22));
        LinearLayout.LayoutParams dockLp=new LinearLayout.LayoutParams(-2,-2);
        dockLp.topMargin=d(12);bottom.addView(dock,dockLp);
        int count=Math.min(5,apps.size());
        for(int i=0;i<count;i++){
            final int index=i;
            FrameLayout tile=makeTile(apps.get(i),index,true);
            LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(d(96),d(76));
            p.setMargins(d(5),0,d(5),0);dock.addView(tile,p);
            tile.setOnClickListener(v->launch(apps.get(index)));
        }
        hintLabel=txt("↓  Tüm uygulamalar   •   OK  Aç",11,0xffaab8cb,false);
        hintLabel.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams hp=new LinearLayout.LayoutParams(-1,-2);hp.topMargin=d(10);
        bottom.addView(hintLabel,hp);
        if(count>0)dock.getChildAt(Math.min(selected,count-1)).requestFocus();
    }
    private FrameLayout makeTile(ResolveInfo info,int index,boolean dock){
        FrameLayout tile=new FrameLayout(this);
        tile.setFocusable(true);tile.setClickable(true);tile.setClipChildren(false);
        tile.setBackground(round(0xff2a3448,17));
        LinearLayout inner=column();inner.setGravity(Gravity.CENTER);
        inner.setPadding(d(7),d(7),d(7),d(7));
        tile.addView(inner,new FrameLayout.LayoutParams(-1,-1));
        ImageView icon=new ImageView(this);
        try{icon.setImageDrawable(info.loadIcon(getPackageManager()));}
        catch(Exception e){icon.setImageResource(android.R.drawable.sym_def_app_icon);}
        icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        inner.addView(icon,new LinearLayout.LayoutParams(d(dock?47:68),d(dock?47:68)));
        if(!dock){
            TextView name=txt(info.loadLabel(getPackageManager()).toString(),12,WHITE,true);
            name.setGravity(Gravity.CENTER);name.setSingleLine(true);
            name.setEllipsize(TextUtils.TruncateAt.END);
            LinearLayout.LayoutParams np=new LinearLayout.LayoutParams(-1,-2);np.topMargin=d(9);
            inner.addView(name,np);
        }
        tile.setOnFocusChangeListener((v,focused)->{
            v.animate().scaleX(focused?1.1f:1f).scaleY(focused?1.1f:1f)
                .setDuration(180).start();
            v.setElevation(focused?d(20):d(1));
            GradientDrawable bg=round(focused?0xff58647c:0xff2a3448,17);
            if(focused){bg.setStroke(d(2),0xfff4f6ff);selected=index;}
            v.setBackground(bg);
        });
        return tile;
    }
    private void showApps(){
        showingApps=true;timeLabel=null;dateLabel=null;
        setupStage();topBar();appTiles.clear();
        ScrollView scroll=new ScrollView(this);
        scroll.setVerticalScrollBarEnabled(false);scroll.setClipToPadding(false);
        FrameLayout.LayoutParams sl=new FrameLayout.LayoutParams(-1,-1);
        sl.topMargin=d(91);stage.addView(scroll,sl);
        LinearLayout body=column();body.setPadding(d(52),d(10),d(52),d(30));
        scroll.addView(body);
        TextView title=txt("Uygulamalar",30,WHITE,true);body.addView(title);
        TextView sub=txt("İzlemek istediğini seç",13,MUTED,false);
        LinearLayout.LayoutParams sp=new LinearLayout.LayoutParams(-1,-2);sp.topMargin=d(7);sp.bottomMargin=d(24);
        body.addView(sub,sp);
        GridLayout grid=new GridLayout(this);grid.setColumnCount(5);body.addView(grid);
        int widthPx=getResources().getDisplayMetrics().widthPixels;
        int available=widthPx-d(104)-d(4*14);
        int tileWidth=Math.max(d(115),available/5);
        for(int i=0;i<apps.size();i++){
            final int idx=i;
            FrameLayout tile=makeTile(apps.get(i),i,false);
            GridLayout.LayoutParams gp=new GridLayout.LayoutParams(GridLayout.spec(i/5),GridLayout.spec(i%5));
            gp.width=tileWidth;gp.height=d(128);gp.setMargins(0,0,d(14),d(14));
            grid.addView(tile,gp);
            tile.setOnClickListener(v->launch(apps.get(idx)));
            appTiles.add(tile);
        }
        if(!appTiles.isEmpty())appTiles.get(Math.min(selected,appTiles.size()-1)).requestFocus();
    }
    private void launch(ResolveInfo info){
        try{
            PackageManager pm=getPackageManager();String pkg=info.activityInfo.packageName;
            Intent intent=pm.getLeanbackLaunchIntentForPackage(pkg);
            if(intent==null)intent=pm.getLaunchIntentForPackage(pkg);
            if(intent==null){intent=new Intent(Intent.ACTION_MAIN);intent.setClassName(pkg,info.activityInfo.name);}
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);startActivity(intent);
        }catch(Exception e){
            android.widget.Toast.makeText(this,"Uygulama açılamadı",android.widget.Toast.LENGTH_SHORT).show();
        }
    }
    @Override public boolean onKeyDown(int key,KeyEvent event){
        if(!showingApps && key==KeyEvent.KEYCODE_DPAD_DOWN){showApps();return true;}
        if(showingApps && (key==KeyEvent.KEYCODE_BACK || key==KeyEvent.KEYCODE_ESCAPE)){showClock();return true;}
        if(key==KeyEvent.KEYCODE_MENU){if(showingApps)showClock();else showApps();return true;}
        return super.onKeyDown(key,event);
    }
    @Override public void onBackPressed(){if(showingApps)showClock();else super.onBackPressed();}
}
