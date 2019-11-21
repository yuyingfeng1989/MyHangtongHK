//package com.bluebud.data.dao;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//
//import com.bluebud.data.SQLiteDDL;
//import com.bluebud.info.PayPalInfo;
//
//public class PayPalDao {
//	private SQLiteDDL ddl;
//
//	public PayPalDao(Context context) {
//		ddl = new SQLiteDDL(context);
//	}
//
//	public void insert(PayPalInfo payPalInfo) {
//		ContentValues values = new ContentValues();
//		values.put(PayPalInfo.USER_NAME, payPalInfo.userName);
//		values.put(PayPalInfo.ORDER_ID, payPalInfo.orderId);
//		values.put(PayPalInfo.TRACKER_NO, payPalInfo.trackerNo);
//		values.put(PayPalInfo.STATE, payPalInfo.state);
//		values.put(PayPalInfo.BODY, payPalInfo.body);
//		values.put(PayPalInfo.PRICE, payPalInfo.price);
//		values.put(PayPalInfo.PACKAGE_ID, payPalInfo.packageId);
//
//		ddl.insert(PayPalInfo.TABLE_NAME, values);
//	}
//
//	public List<PayPalInfo> query(String sUserName) {
//		String selection = PayPalInfo.USER_NAME + " = ?";
//		Cursor mCursor = ddl
//				.query(PayPalInfo.TABLE_NAME, null, selection,
//						new String[] { sUserName }, null, null, PayPalInfo.ID
//								+ " DESC");
//		if (mCursor != null) {
//			List<PayPalInfo> payPalInfos = new ArrayList<PayPalInfo>();
//			while (mCursor.moveToNext()) {
//				PayPalInfo payPalInfo = new PayPalInfo();
//				payPalInfo.userName = mCursor.getString(mCursor
//						.getColumnIndex(PayPalInfo.USER_NAME));
//				payPalInfo.orderId = mCursor.getString(mCursor
//						.getColumnIndex(PayPalInfo.ORDER_ID));
//				payPalInfo.trackerNo = mCursor.getString(mCursor
//						.getColumnIndex(PayPalInfo.TRACKER_NO));
//				payPalInfo.state = mCursor.getInt(mCursor
//						.getColumnIndex(PayPalInfo.STATE));
//				payPalInfo.body = mCursor.getString(mCursor
//						.getColumnIndex(PayPalInfo.BODY));
//				payPalInfo.price = mCursor.getString(mCursor
//						.getColumnIndex(PayPalInfo.PRICE));
//				payPalInfo.packageId = mCursor.getString(mCursor
//						.getColumnIndex(PayPalInfo.PACKAGE_ID));
//
//				payPalInfos.add(payPalInfo);
//			}
//			mCursor.close();
//			return payPalInfos;
//		} else {
//			return null;
//		}
//	}
//
//	/**
//	 *
//	 * @param orderId
//	 * @param state
//	 *            0:验证 1:验证通过
//	 */
//	public void update(PayPalInfo payPalInfo) {
//		ContentValues values = new ContentValues();
//		values.put(PayPalInfo.USER_NAME, payPalInfo.userName);
//		values.put(PayPalInfo.ORDER_ID, payPalInfo.orderId);
//		values.put(PayPalInfo.TRACKER_NO, payPalInfo.trackerNo);
//		values.put(PayPalInfo.STATE, payPalInfo.state);
//		values.put(PayPalInfo.BODY, payPalInfo.body);
//		values.put(PayPalInfo.PRICE, payPalInfo.price);
//		values.put(PayPalInfo.PACKAGE_ID, payPalInfo.packageId);
//
//		String selection = PayPalInfo.ORDER_ID + " = ?";
//		ddl.update(PayPalInfo.TABLE_NAME, values, selection,
//				new String[] { payPalInfo.orderId });
//		ddl.close();
//	}
//
//	public void close() {
//		ddl.close();
//	}
//}
