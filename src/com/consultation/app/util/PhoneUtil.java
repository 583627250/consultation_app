package com.consultation.app.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.conn.util.InetAddressUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;

@SuppressLint("DefaultLocale")
public class PhoneUtil {

    private static PhoneUtil instance;

    public static PhoneUtil getInstance() {
        if(instance == null) {
            instance=new PhoneUtil();
        }
        return instance;
    }

    /**
     * @return 返回手机IP地址
     */
    public String getLocalIpAddress() {
        try {
            for(Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf=en.nextElement();
                for(Enumeration<InetAddress> enumIpAddr=intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress=enumIpAddr.nextElement();
                    if(!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(inetAddress.getHostAddress())) {
                        return inetAddress.getHostAddress().toString() + GetNetIp("http://fw.qq.com/ipaddress");
                    }
                }
            }
        } catch(SocketException e) {
            Log.e("WifiPreference IpAddress", e.toString());
        }
        return null;
    }

    String GetNetIp(String ipaddr) {
        URL infoUrl=null;
        InputStream inStream=null;
        try {
            infoUrl=new URL(ipaddr);
            URLConnection connection=infoUrl.openConnection();
            HttpURLConnection httpConnection=(HttpURLConnection)connection;
            int responseCode=httpConnection.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK) {
                inStream=httpConnection.getInputStream();
                BufferedReader reader=new BufferedReader(new InputStreamReader(inStream, "utf-8"));
                StringBuilder strber=new StringBuilder();
                String line=null;
                while((line=reader.readLine()) != null)
                    strber.append(line + "\n");
                inStream.close();
                return strber.toString();
            }
        } catch(MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch(IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    /**
     * @param ctx
     * @return MAC地址
     */
    public String getLocalMacAddress(Context ctx) {
        try {
            WifiManager wifi=(WifiManager)ctx.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info=wifi.getConnectionInfo();
            return info.getMacAddress();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param ctx
     * @return 手机号码
     */
    public String getTelePhoneNumber(Context ctx) {
    	TelephonyManager phoneMgr = null;
        try {
            phoneMgr=(TelephonyManager)ctx.getSystemService(Context.TELEPHONY_SERVICE);
        } catch(Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return phoneMgr.getLine1Number();
    }

    /**
     * @param ctx
     * @return 链接网络类型
     */
    public String getNetType(Context ctx) {
        try {
            ConnectivityManager connectivityManager=(ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetInfo=connectivityManager.getActiveNetworkInfo();
            return activeNetInfo.getTypeName();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info=connectivity.getAllNetworkInfo();
            if(info != null) {
                for(int i=0; i < info.length; i++) {
                    if(info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 没有连接网络，提示是否对网络进行设置
     * @param context
     */
    @SuppressLint("NewApi")
    public static void netWorkStatusAndSetting(final Context context) {
        Builder b=new AlertDialog.Builder(context).setTitle("没有可用的网络").setMessage("是否对网络进行设置？");
        b.setPositiveButton("是", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                Intent mIntent=new Intent("/");
                ComponentName comp=new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
                mIntent.setComponent(comp);
                mIntent.setAction("android.intent.action.VIEW");
                if(android.os.Build.VERSION.SDK_INT > 10) {
                    context.startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                } else {
                    context.startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                }
                System.exit(0);
            }

        }).setNeutralButton("否", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
                System.exit(0);
            }
        }).show();

    }

    /**
     * @param ctx
     * @return 所在位子服务
     */
    public String getLocation(Context ctx) {
    	Location lm = null;
        LocationManager m_location_manager=(LocationManager)ctx.getSystemService(Context.LOCATION_SERVICE);
        lm=m_location_manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        return lm.toString();
    }

    /**
     * @param ctx
     * @return 设备唯一串号ID
     */
    public String getDeviceId(Context ctx) {
    	 /**we make this look like a valid IMEI */
    	String m_szDevIDShort = "35" +
    			Build.BOARD.length()%10 + 
    			Build.BRAND.length()%10 + 
    			Build.CPU_ABI.length()%10 + 
    			Build.DEVICE.length()%10 + 
    			Build.DISPLAY.length()%10 + 
    			Build.HOST.length()%10 + 
    			Build.ID.length()%10 + 
    			Build.MANUFACTURER.length()%10 + 
    			Build.MODEL.length()%10 + 
    			Build.PRODUCT.length()%10 + 
    			Build.TAGS.length()%10 + 
    			Build.TYPE.length()%10 + 
    			Build.USER.length()%10;
    	
    	String m_szAndroidID = Secure.getString(ctx.getContentResolver(), Secure.ANDROID_ID);
    	
    	BluetoothAdapter m_BluetoothAdapter = null; // Local Bluetooth adapter      
 		String m_szBTMAC="";
		m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();      
		if(m_BluetoothAdapter != null) {
		m_szBTMAC = m_BluetoothAdapter.getAddress();
		}
		
		String  m_szUniqueID = getImei(ctx) + m_szDevIDShort + m_szAndroidID + getLocalMacAddress(ctx) + m_szBTMAC;
		// compute md5     
		 MessageDigest m = null;   
		try {
		 m = MessageDigest.getInstance("MD5");
		 } catch (NoSuchAlgorithmException e) {
		 e.printStackTrace();   
		}    
		m.update(m_szUniqueID.getBytes(),0,m_szUniqueID.length());   
		// get md5 bytes   
		byte p_md5Data[] = m.digest();   
		String deviceId = new String();   
		for (int i=0;i<p_md5Data.length;i++) {   
		     int b =  (0xFF & p_md5Data[i]);    
		// if it is a single digit, make sure it have 0 in front (proper padding)    
		    if (b <= 0xF) 
		    	deviceId+="0";    
		// add number to string    
		    deviceId+=Integer.toHexString(b); 
		   }   // hex string to uppercase   
		deviceId = deviceId.toUpperCase();
        return deviceId;
    }
    
    /**
     * @param ctx
     * @return 设备Imei
     */
    public String getImei(Context ctx){
    	TelephonyManager TelephonyMgr = (TelephonyManager)ctx.getSystemService(Context.TELEPHONY_SERVICE);
    	String imei = TelephonyMgr.getDeviceId(); 
    	return imei;
    }
    
    /**
     * @param ctx
     * @return 手机系统版本号
     */
    public String getDeviceSoftwareVersion(Context ctx){
    	TelephonyManager telephonyManager=(TelephonyManager)ctx.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceSoftwareVersion=telephonyManager.getDeviceSoftwareVersion();
        return deviceSoftwareVersion;
    }
    
    /**
     * @param ctx
     * @return 设备型号
     */
    public String getDeviceModel(Context ctx){
    	String deviceModel = android.os.Build.MODEL; // 手机型号  
    	return deviceModel;
    }
    
    /**
     * @param ctx
     * @return 设备系统版本
     */
    public String getReleaseMdoel(Context ctx){
    	String release = android.os.Build.VERSION.RELEASE;//设备系统版本
    	return release;
    }
    
	/**
	 * @param ctx
	 * @return 手机安装APP信息
	 */
    int resultId = 0;
//    String [] result;
	public String[] getAllApp(final Context ctx) {
		List<PackageInfo> packages = ctx.getPackageManager().getInstalledPackages(0);
		List<String> apps = new ArrayList<String>();
		
//		for (PackageInfo i : packages) {
//				String res= i.applicationInfo.loadLabel(ctx.getPackageManager()).toString();
//				String str2 = res.replaceAll("", "");  
//				result[resultId] = str2;
//				resultId++;
//		}
		for(int i = 0;i<packages.size();i++){
			PackageInfo pak = (PackageInfo) packages.get(i);
			if ((pak.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
				String res= pak.applicationInfo.loadLabel(ctx.getPackageManager()).toString();
				String str2 = res.replaceAll("", "");  
				apps.add(str2);
			}
		}
		String [] result = new String[apps.size()];
		for(int j = 0;j<apps.size();j++){
			String appItem = apps.get(j);
			result[j] = appItem;
		}
		
		
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				PackageManager pManager = ctx.getPackageManager();  
//			    //获取手机内所有应用  
//			    List<PackageInfo> paklist = pManager.getInstalledPackages(0);  
//			    result = new String[paklist.size()];
//			    for (int i = 0; i < paklist.size(); i++) {  
//			        PackageInfo pak = (PackageInfo) paklist.get(i);  
//			        //判断是否为非系统预装的应用程序  
//			        if ((pak.applicationInfo.flags & pak.applicationInfo.FLAG_SYSTEM) <= 0) {  
//			            // customs applications  
//			            String res= pak.applicationInfo.loadLabel(ctx.getPackageManager()).toString();
//			            String str2 = res.replaceAll("", "");
//			            result[i] = str2;
//			            System.out.println(str2);
//			        }  
//			    } 
//			} 
//			
//		}).start();
		
		return result;
	}
	
	/**
	 * @param ctx
	 * @return 手机号码所在归属地
	 */
	public String getRegion(Context ctx) {
		String region = null;
		try {
			region = getMobileLocation(getTelePhoneNumber(ctx), 2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return region;
	}
	
	/**
	 * @param ctx
	 * @return 通讯服务商
	 */
	public String getServiceProviders(Context ctx){
		String serviceProviders = null;
		try {
			serviceProviders = getMobileLocation(getTelePhoneNumber(ctx),1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return serviceProviders;
	}
	
	//判断手机号码的归属点和服务商==============================================================//
	/**
	 * 归属地查询
	 * 
	 * @param mobile
	 * @return mobileAddress
	 */
	private static String getLocationByMobile(final String mobile)
			throws ParserConfigurationException, SAXException, IOException {
		String MOBILEURL = " http://www.youdao.com/smartresult-xml/search.s?type=mobile&q=";
		String result = callUrlByGet(MOBILEURL + mobile, "GBK");
		StringReader stringReader = new StringReader(result);
		InputSource inputSource = new InputSource(stringReader);
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory
				.newDocumentBuilder();
		Document document = documentBuilder.parse(inputSource);

		if (!(document.getElementsByTagName("location").item(0) == null)) {
			return document.getElementsByTagName("location").item(0)
					.getFirstChild().getNodeValue();
		} else {
			return "无此号记录！";
		}
	}

	/**
	 * 获取URL返回的字符串
	 * 
	 * @param callurl
	 * @param charset
	 * @return
	 */
	private static String callUrlByGet(String callurl, String charset) {
		String result = "";
		try {
			URL url = new URL(callurl);
			URLConnection connection = url.openConnection();
			connection.connect();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream(), charset));
			String line;
			while ((line = reader.readLine()) != null) {
				result += line;
				result += "\n";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		return result;
	}

	/**
	 * 手机号码归属地
	 * 
	 * @param tel
	 *            手机号码
	 * @param type
	 *            获取通讯服务商1,获取归属地2
	 * @return 135XXXXXXXX, 例子 ：联通/移动/电信,江西抚州
	 * @throws Exception
	 * @author JIA-G-Y
	 */
	public static String getMobileLocation(String tel, int type)
			throws Exception {
		if (tel == null) {
			return null;
		}
		String context = null;
		Pattern pattern = Pattern.compile("1\\d{10}");
		Matcher matcher = pattern.matcher(tel);
		if (matcher.matches()) {
			String url = "http://life.tenpay.com/cgi-bin/mobile/MobileQueryAttribution.cgi?chgmobile="
					+ tel;
			String result = callUrlByGet(url, "GBK");
			StringReader stringReader = new StringReader(result);
			InputSource inputSource = new InputSource(stringReader);
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory
					.newDocumentBuilder();
			Document document = documentBuilder.parse(inputSource);
			String retmsg = document.getElementsByTagName("retmsg").item(0)
					.getFirstChild().getNodeValue();
			if (retmsg.equals("OK")) {
				String supplier = document.getElementsByTagName("supplier")
						.item(0).getFirstChild().getNodeValue().trim();
				String province = document.getElementsByTagName("province")
						.item(0).getFirstChild().getNodeValue().trim();
				String city = document.getElementsByTagName("city").item(0)
						.getFirstChild().getNodeValue().trim();
				if (province.equals("-") || city.equals("-")) {
					// return (tel + "," + supplier + "," +
					// getLocationByMobile(tel));
					if (type == 1) {
						context = supplier;
					} else if (type == 2) {
						context = getLocationByMobile(tel);
					}
				} else {
					// return (tel + "," + supplier + "," + province +","+
					// city);
					if (type == 1) {
						context = supplier;
					} else if (type == 2) {
						context = province + "," + city;
					}
				}
			} else {
				return "无此号记录！";
			}
		} else {
			return tel + "：手机号码格式错误！";
		}
		return context;
	}
	
	/**
     * 账号登出时间和ticket值失效后的毫秒值
     */
    private long logoutTime;
    public long getLogOutTime(){
    	return logoutTime;
    }
    
    public void setLogOutTime(long logoutTime){
    	this.logoutTime = logoutTime;
    }
    
    //========================判断手机是否root=========================//
    private final static int kSystemRootStateUnknow = -1;
	private final static int kSystemRootStateDisable = 0;
	private final static int kSystemRootStateEnable = 1;
	private static int systemRootState = kSystemRootStateUnknow;
    public boolean isRootSystem() {
		if (systemRootState == kSystemRootStateEnable) {
			return true;
		} else if (systemRootState == kSystemRootStateDisable) {
			return false;
		}
		File f = null;
		final String kSuSearchPaths[] = { "/system/bin/", "/system/xbin/",
				"/system/sbin/", "/sbin/", "/vendor/bin/" };
		try {
			for (int i = 0; i < kSuSearchPaths.length; i++) {
				f = new File(kSuSearchPaths[i] + "su");
				if (f != null && f.exists()) {
					systemRootState = kSystemRootStateEnable;
					return true;
				}
			}
		} catch (Exception e) {
		}
		systemRootState = kSystemRootStateDisable;
		return false;
	}
    
    /**
     * 判断是否是平板
     * @param context
     * @return
     */
     public boolean isTablet(Context ctx) {
         return (ctx.getResources().getConfiguration().screenLayout
                 & Configuration.SCREENLAYOUT_SIZE_MASK)
                 >= Configuration.SCREENLAYOUT_SIZE_LARGE;
     }
}
