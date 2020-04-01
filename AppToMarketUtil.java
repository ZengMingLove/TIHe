package com.zbht.hgb.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.RomUtils;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zbht.hgb.common.Constant;

import java.util.List;

/**
 * Author: ming.zeng
 * Date: 19/12/2019 上午 10:26
 * Desc: app跳转应用市场工具类
 * com.android.vending         Google Play
 * com.tencent.android.qqdownloader        应用宝
 * com.qihoo.appstore      360手机助手
 * com.baidu.appsearch 百度手机助
 * com.xiaomi.market   小米应用商店
 * com.wandoujia.phoenix2  豌豆荚
 * com.huawei.appmarket    华为应用市场
 * com.taobao.appcenter    淘宝手机助手
 * com.hiapk.marketpho  安卓市场
 * com.bbk.appstore vivo应用市场
 * com.oppo.market  oppo应用市场
 */
public class AppToMarketUtil {

    private static final String TAG = "AppToMarketUtil";
    public static final String MARKET_GOOGLE = "com.android.vending";
    public static final String MARKET_HUAWEI = "com.huawei.appmarket";
    public static final String MARKET_XIAOMI = "com.xiaomi.market";
    public static final String MARKET_OPPO = "com.oppo.market";
    public static final String MARKET_VIVO = "com.bbk.appstore";

    public static final String WX_PACKAGE = "com.tencent.mm"; // 微信包名
    public static final String ZFB_PACKAGE = "com.eg.android.AlipayGphone"; // 支付宝包名

    /**
     * 跳转系统应用商店的微信
     *
     * @return 跳转成功返回 true
     */
    public static boolean appToMarketWx(Context context) {
        return appToMarket(context, WX_PACKAGE);
    }

    /**
     * 跳转系统应用商店的支付宝
     *
     * @return 跳转成功返回 true
     */
    public static boolean appToMarketAli(Context context) {
        return appToMarket(context, ZFB_PACKAGE);
    }


    /**
     * 跳转系统应用商店
     *
     * @return 跳转成功返回 true
     */
    private static boolean appToMarket(Context context, String appPkg) {
        String market;
        if (RomUtils.isHuawei()) {
            market = MARKET_HUAWEI;
        } else if (RomUtils.isXiaomi()) {
            market = MARKET_XIAOMI;
        } else if (RomUtils.isOppo()) {
            market = MARKET_OPPO;
        } else if (RomUtils.isVivo()) {
            market = MARKET_VIVO;
        } else {
            return false;
        }
        Log.d(TAG, "appToMarket: appPkg = " + appPkg);
        Log.d(TAG, "appToMarket: market = " + market);

        if (isAppMarketSystem(market)) {
            Log.d(TAG, "appToMarket: 是系统商店");
            jumpToAppMarket(context, appPkg, market);
        } else {
            Log.d(TAG, "appToMarket: 有应用商店，但不是系统商店");
            if (isAppMarketSystem(MARKET_GOOGLE)) {
                Log.d(TAG, "appToMarket: 是系统Google商店");
                jumpToAppMarket(context, appPkg, MARKET_GOOGLE);
            } else {
                Log.d(TAG, "appToMarket: 没有系统商店");
                return false;
            }
        }
        return true;
    }

    /**
     * 是否是支持的品牌
     * @return
     */
    public static boolean isSupportBrand() {
        return RomUtils.isHuawei() || RomUtils.isXiaomi() || RomUtils.isOppo() || RomUtils.isVivo();
    }

    /**
     * 判断应用是否是系统应用
     * @param marketPkg 应用包名
     * @return true 是系统应用
     */
    public static boolean isAppMarketSystem(String marketPkg) {
        return AppUtils.isAppSystem(marketPkg);
    }

    /**
     * 启动到应用商店app详情界面
     *
     * @param appPkg    目标App的包名
     * @param marketPkg 应用商店包名 ,如果为""则由系统弹出应用商店列表供用户选择,否则调转到目标市场的应用详情界面，某些应用商店可能会失败
     */
    private static void jumpToAppMarket(Context context, String appPkg, String marketPkg) {
        try {
            if (TextUtils.isEmpty(appPkg)) {
                return;
            }

            Uri uri = Uri.parse("market://details?id=" + appPkg);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (!TextUtils.isEmpty(marketPkg)) {
                intent.setPackage(marketPkg);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 后台商店字段
     * @return
     */
    public static String getAppMarket() {
        String marketStr;
        if (isSupportBrand()) {
            String marketPkg = "";
            if (RomUtils.isHuawei()) {
                marketPkg = MARKET_HUAWEI;
            } else if (RomUtils.isXiaomi()) {
                marketPkg = MARKET_XIAOMI;
            } else if (RomUtils.isOppo()) {
                marketPkg = MARKET_OPPO;
            } else if (RomUtils.isVivo()) {
                marketPkg = MARKET_VIVO;
            }

            if (isAppMarketSystem(marketPkg)) {
                marketStr = "系统商店";
            } else {
                if (isAppMarketSystem(MARKET_GOOGLE)) {
                    marketStr = "谷歌商店";
                } else {
                    marketStr = "无商店";
                }
            }
        } else {
            marketStr = "不支持品牌";
        }
        return marketStr;
    }

}
