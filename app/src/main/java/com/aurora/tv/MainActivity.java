package com.aurora.tv;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.List;

public class MainActivity extends Activity {
 int white=Color.WHITE, muted=Color.rgb(168,178,196);
 @Override public void onCreate(Bundle state){super.onCreate(state); render();}
 GradientDrawable bg(int color,int radius){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(radius);return d;}
 TextView text(String s,int size,int color,boolean bold){TextView v=new TextView(this);v.setText(s);v.setTextSize(size);v.setTextColor(color);if(bold)v.setTypeface(null,Typeface.BOLD);return v;}
 void render(){
  ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);scroll.setBackground(new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{0xff101f3b,0xff080b14,0xff17132a}));
  LinearLayout root=new LinearLayout(this);root.setOrientation(1);root.setPadding(60,35,45,50);scroll.addView(root);setContentView(scroll);
  TextView title=text("◉  AURORA",27,white,true);root.addView(title);
  TextView tagline=text("YOUR SPACE. YOUR SCREEN.",13,muted,false);tagline.setPadding(0,8,0,40);root.addView(tagline);
  LinearLayout hero=new LinearLayout(this);hero.setOrientation(1);hero.setPadding(34,28,34,32);hero.setBackground(bg(0xff263a5c,28));root.addView(hero,new LinearLayout.LayoutParams(-1,210));
  TextView eyebrow=text("WELCOME TO AURORA TV",14,0xffc4d6ff,true);hero.addView(eyebrow);
  TextView headline=text("Everything you love.\nOne beautiful place.",33,white,true);headline.setPadding(0,18,0,8);hero.addView(headline);
  TextView note=text("Choose an app below using your TV remote",15,0xffcbd6eb,false);hero.addView(note);
  TextView appsTitle=text("Your Apps",24,white,true);appsTitle.setPadding(0,35,0,20);root.addView(appsTitle);
  HorizontalScrollView horizontal=new HorizontalScrollView(this);horizontal.setHorizontalScrollBarEnabled(false);root.addView(horizontal);
  LinearLayout row=new LinearLayout(this);row.setOrientation(0);horizontal.addView(row);
  PackageManager pm=getPackageManager();Intent query=new Intent(Intent.ACTION_MAIN);query.addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER);
  List<ResolveInfo> apps=pm.queryIntentActivities(query,0);int count=0;
  for(ResolveInfo info:apps){if(info.activityInfo.packageName.equals(getPackageName()))continue;String label=info.loadLabel(pm).toString();String pkg=info.activityInfo.packageName;
   TextView tile=text(label,18,white,true);tile.setGravity(Gravity.CENTER);tile.setPadding(16,12,16,12);tile.setBackground(bg(0xff243047,20));tile.setFocusable(true);tile.setOnFocusChangeListener((v,focused)->{v.setScaleX(focused?1.08f:1f);v.setScaleY(focused?1.08f:1f);v.setBackground(bg(focused?0xff446caa:0xff243047,20));});tile.setOnClickListener(v->{Intent launch=pm.getLeanbackLaunchIntentForPackage(pkg);if(launch==null)launch=pm.getLaunchIntentForPackage(pkg);if(launch!=null)startActivity(launch);});
   LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(220,115);lp.setMargins(0,0,18,10);row.addView(tile,lp);count++;
  }
  if(count==0){TextView empty=text("No TV apps detected",18,muted,false);row.addView(empty);}
  TextView footer=text("Aurora TV 1.0  •  Press HOME to choose your launcher",13,muted,false);footer.setPadding(0,40,0,0);root.addView(footer);
 }
}