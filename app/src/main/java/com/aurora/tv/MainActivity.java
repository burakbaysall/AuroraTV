package com.aurora.tv;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.LinearGradient;
import android.graphics.Shader;
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
    private int scenery=0;
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
        stage.setBackgroundColor(0xff0a111b);
        stage.addView(new Landscape(this),new FrameLayout.LayoutParams(-1,-1));
        setContentView(stage);
    }
    private void topBar(){
        LinearLayout top=new LinearLayout(this);top.setGravity(Gravity.CENTER_VERTICAL);
        FrameLayout.LayoutParams lp=new FrameLayout.LayoutParams(-1,d(46),Gravity.TOP);
        lp.setMargins(d(52),d(24),d(52),0);stage.addView(top,lp);
        TextView brand=txt("✦  AURORA",17,WHITE,true);brand.setLetterSpacing(.13f);
        top.addView(brand,new LinearLayout.LayoutParams(0,-2,1));
        addPill(top,"☀  Manzara",()->{scenery=(scenery+1)%3; if(showingApps)showApps();else showClock();});
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
        cp.bottomMargin=d(110);stage.addView(center,cp);
        dateLabel=txt("",17,0xfff1f3f7,false);dateLabel.setGravity(Gravity.CENTER);
        center.addView(dateLabel);
        timeLabel=txt("",128,0xfff1f2fa,false);timeLabel.setGravity(Gravity.CENTER);
        timeLabel.setTypeface(Typeface.create("sans-serif-condensed-light",Typeface.NORMAL));
        timeLabel.setLetterSpacing(-.055f);
        LinearLayout.LayoutParams tp=new LinearLayout.LayoutParams(-1,-2);tp.topMargin=d(2);
        center.addView(timeLabel,tp);
        dateLabel.setText(new SimpleDateFormat("EEEE, d MMMM",new Locale("tr","TR")).format(new Date()));
        timeLabel.setText(new SimpleDateFormat("H:mm",Locale.getDefault()).format(new Date()));
        LinearLayout bottom=column();bottom.setGravity(Gravity.CENTER);
        FrameLayout.LayoutParams bp=new FrameLayout.LayoutParams(-1,-2,Gravity.BOTTOM);
        bp.bottomMargin=d(18);stage.addView(bottom,bp);
        TextView section=txt("UYGULAMALAR",11,0xffbdc9da,true);
        section.setLetterSpacing(.16f);section.setGravity(Gravity.CENTER);bottom.addView(section);
        LinearLayout dock=new LinearLayout(this);dock.setGravity(Gravity.CENTER);
        dock.setPadding(d(13),d(10),d(13),d(10));
        dock.setBackground(round(0x9940474d,19));
        LinearLayout.LayoutParams dockLp=new LinearLayout.LayoutParams(-2,-2);
        dockLp.topMargin=d(12);bottom.addView(dock,dockLp);
        int count=Math.min(5,apps.size());
        for(int i=0;i<count;i++){
            final int index=i;
            FrameLayout tile=makeTile(apps.get(i),index,true);
            LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(d(103),d(76));
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
        tile.setBackground(round(0xcc303a4a,16));
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
            GradientDrawable bg=round(focused?0xff74869a:0xcc303a4a,16);
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
        scroll.setBackgroundColor(0x55060b12);
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
    // Lightweight offline landscape renderer. No internet, photo assets, or GPU shaders required.
    private class Landscape extends View {
        private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Path path=new Path();
        Landscape(android.content.Context context){super(context);setLayerType(View.LAYER_TYPE_SOFTWARE,null);}
        private void poly(Canvas c,int color,float... xy){
            p.setShader(null);p.setColor(color);p.setStyle(Paint.Style.FILL);
            path.reset();path.moveTo(xy[0],xy[1]);
            for(int i=2;i<xy.length;i+=2)path.lineTo(xy[i],xy[i+1]);
            path.close();c.drawPath(path,p);
        }
        private void rect(Canvas c,int color,float l,float t,float r,float b){
            p.setShader(null);p.setColor(color);c.drawRect(l,t,r,b,p);
        }
        @Override protected void onDraw(Canvas actual){
            super.onDraw(actual);
            int w=getWidth(),h=getHeight();if(w<=0||h<=0)return;
            Canvas c=actual;int save=c.save();c.scale(w/1000f,h/560f);
            int skyTop=scenery==1?0xff174d65:scenery==2?0xff172b4a:0xff233e52;
            int skyBottom=scenery==1?0xffb6d8d2:scenery==2?0xffd4a88a:0xffc8c8b5;
            p.setShader(new LinearGradient(0,0,0,360,skyTop,skyBottom,Shader.TileMode.CLAMP));
            c.drawRect(0,0,1000,560,p);p.setShader(null);
            // distant mountain silhouettes and bright snow ridges
            poly(c,0xff647e8b,0,270,95,200,145,244,240,150,310,242,394,176,490,259,575,170,662,252,740,190,825,255,930,182,1000,246,1000,420,0,420);
            poly(c,0xffd3dbe0,198,183,240,150,278,195,259,188,240,171,225,191);
            poly(c,0xffe4e6e0,353,215,394,176,430,214,408,203,393,190,382,207);
            poly(c,0xffdbe2e3,541,211,575,170,619,224,593,204,575,184,560,202);
            poly(c,0xffb6cbd1,708,228,740,190,781,236,752,220,740,201,727,217);
            poly(c,0xff425e64,0,300,105,255,204,290,290,249,395,298,480,265,580,301,710,258,825,306,930,254,1000,300,1000,430,0,430);
            // turquoise alpine lake
            p.setShader(new LinearGradient(0,295,0,560,0xff438b94,0xff12384c,Shader.TileMode.CLAMP));
            c.drawRect(0,295,1000,560,p);p.setShader(null);
            // quiet horizontal water reflections
            for(int i=0;i<17;i++){
                float y=308+i*13f;
                int alpha=30+(i%4)*9;
                rect(c,Color.argb(alpha,200,238,230),((i*71)%730),y,Math.min(1000,((i*71)%730)+90+(i%3)*52),y+1.4f);
            }
            // near shoreline and forest edge
            poly(c,0xff183b38,0,282,70,260,135,278,206,267,272,288,315,294,0,328);
            poly(c,0xff183d38,690,290,745,272,816,283,888,262,1000,280,1000,335,705,316);
            // dark foreground adds contrast to translucent dock
            poly(c,0xff142b2b,0,498,110,475,230,506,340,484,500,514,630,487,740,516,870,485,1000,508,1000,560,0,560);
            // cinematic legibility vignette top and bottom
            p.setShader(new LinearGradient(0,0,0,560,new int[]{0x8807111e,0x0507111e,0x1107111e,0xb9091018},new float[]{0f,.28f,.62f,1f},Shader.TileMode.CLAMP));
            c.drawRect(0,0,1000,560,p);p.setShader(null);
            c.restoreToCount(save);
        }
    }

}
