package com.bluebud.activity.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bluebud.activity.BaseActivity;
import com.bluebud.adapter.PayWayAdapter;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.http.HttpParams;
import com.bluebud.info.OrderPackageInfo;
import com.bluebud.info.PaypalVerifyInfo;
import com.bluebud.info.ReBaseObj;
import com.bluebud.info.Tracker;
import com.bluebud.utils.Constants;
import com.bluebud.utils.GsonParse;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.PopupWindowListPositionUtils;
import com.bluebud.utils.PopupWindowListPositionUtils.ListPositon;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.utils.Utils;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalItem;
import com.paypal.android.sdk.payments.PayPalOAuthScopes;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalPaymentDetails;
import com.paypal.android.sdk.payments.PayPalProfileSharingActivity;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.paypal.android.sdk.payments.ShippingAddress;

import org.apache.http.Header;
import org.json.JSONException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class PayWayActivity extends BaseActivity implements OnClickListener, ListPositon, ListView.OnItemClickListener, ProgressDialogUtil.OnProgressDialogClickListener {
    /**
     * - Set to PayPalConfiguration.ENVIRONMENT_PRODUCTION to move real money.
     * <p>
     * - Set to PayPalConfiguration.ENVIRONMENT_SANDBOX to use your test credentials
     * from https://developer.paypal.com
     * <p>
     * - Set to PayPalConfiguration.ENVIRONMENT_NO_NETWORK to kick the tires
     * without communicating to PayPal's servers.
     */
    // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
    // or live (ENVIRONMENT_PRODUCTION)
//    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;//测试环境
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_PRODUCTION;//正试环境

    //这个写入支付ID//mensheng1990-facilitator@163.com
    // note that these credentials will differ between live & sandbox environments.config_client_id
    //private static final String CONFIG_CLIENT_ID = "credentials from developer.paypal.com";
    private String CONFIG_CLIENT_ID = "AQU1z6rKnQVokdg0VXg3VTsxkolEP1YduggZLxuJGh16gwSz_qZhP-feq09K2AjLYw2OQNKM9JJzoRH0";

    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;
    private static final int REQUEST_CODE_PROFILE_SHARING = 3;

    private PayPalConfiguration config;

    private TextView tvPackage;
    // The following are only used in PayPalFuturePaymentActivity.
    //.merchantName("Example Merchant")
    //.merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"))
    //.merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"));

    private TextView tvMoneyAmount;

    private TextView tvServicetime;
    private PopupWindowListPositionUtils positionUtils;
    private List<String> listString;
    private PayWayAdapter adapter;
    private RequestHandle requestHandle;
    private String strTrackerNo;
    private List<OrderPackageInfo.PackageListBean> mPackageListBean;
    private String tv_package_name;
    private String tv_money;
    private String currency_unit;
    private String orderPackageId;
    private Tracker mCurTracker;
    private List<String> currencyAll = Arrays.asList("AUD", "CAD", "EUR", "GBP", "JPY", "USD", "NZD", "CHF", "HKD", "SGD",
            "SEK", "DKK", "PLN", "NOK", "HUF", "CZK", "ILS", "MXN", "BRL", "MYR", "PHP", "TWD", "THB", "TRY", "INR", "RUB");
    private Button mBtnConfirmPayment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_pay_way);
        // PayPalConfiguration.ENVIRONMENT_PRODUCTION
        // ENVIRONMENT_NO_NETWORK
        //environment_no_network;production
        init();
        getOrderPackage(strTrackerNo);
    }

    private void init() {
        super.setBaseTitleText(R.string.pay);
        super.setBaseTitleVisible(View.VISIBLE);
        super.getBaseTitleLeftBack().setOnClickListener(this);
        mCurTracker = UserUtil.getCurrentTracker(this);
        if (mCurTracker != null) {
            strTrackerNo = mCurTracker.device_sn;
        }
        ListView mLiPackage = (ListView) findViewById(R.id.ll_package);
        mBtnConfirmPayment = (Button) findViewById(R.id.btn_confirm_payment);
        mBtnConfirmPayment.setOnClickListener(this);

        adapter = new PayWayAdapter(this, mPackageListBean);
        mLiPackage.setAdapter(adapter);
        mLiPackage.setOnItemClickListener(this);
    }

    public void onProgressDialogBack() {
        LogUtil.i("onProgressDialogBack()");
        if (null != requestHandle && !requestHandle.isFinished()) {
            requestHandle.cancel(true);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        LogUtil.i("ItemClick: postion:" + position);
        adapter.updata(position);
        if (mPackageListBean != null) {
            tv_package_name = mPackageListBean.get(position).getName();
            tv_money = mPackageListBean.get(position).getServe_fee() + "";
            currency_unit = mPackageListBean.get(position).getCurrency_unit();
            if (!Utils.isEmpty(currency_unit)) {
                currency_unit = currency_unit.toUpperCase();
            }
            orderPackageId = mPackageListBean.get(position).getOrderPackageId();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_title_back:
                finish();
                break;
            case R.id.btn_confirm_payment:
                if (currencyAll.contains(currency_unit)) {
                    if (!Utils.isEmpty(tv_money) && !Utils.isEmpty(tv_package_name)) {
                        onBuyPressed();
                    }
                } else {
                    ToastUtil.show(PayWayActivity.this, R.string.do_not_pay_the_currency);
                }

                break;

            default:
                break;
        }

    }


    public void onBuyPressed() {
        /* 
         * PAYMENT_INTENT_SALE will cause the payment to complete immediately.
         * Change PAYMENT_INTENT_SALE to 
         *   - PAYMENT_INTENT_AUTHORIZE to only authorize payment and capture funds later.
         *   - PAYMENT_INTENT_ORDER to create a payment for authorization and capture
         *     later via calls from your server.
         * 
         * Also, to include additional payment details and an item list, see getStuffToBuy() below.
         */
        PayPalPayment thingToBuy = getThingToBuy(PayPalPayment.PAYMENT_INTENT_SALE);

        /*
         * See getStuffToBuy(..) for examples of some available payment options.
         */

        Intent intent = new Intent(PayWayActivity.this, PaymentActivity.class);

        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);

        startActivityForResult(intent, REQUEST_CODE_PAYMENT);

    }

    private PayPalPayment getThingToBuy(String paymentIntent) {

        if (!Utils.isEmpty(currency_unit)) {
            String unit = currency_unit.toUpperCase();
            return new PayPalPayment(new BigDecimal(tv_money), unit, tv_package_name,
                    paymentIntent);
        } else {
            return new PayPalPayment(new BigDecimal(tv_money), "USD", tv_package_name,
                    paymentIntent);
        }

    }

    /* 
     * This method shows use of optional payment details and item list.
     */
    private PayPalPayment getStuffToBuy(String paymentIntent) {
        //--- include an item list, payment amount details
        PayPalItem[] items =
                {
                        new PayPalItem("sample item #1", 2, new BigDecimal("87.50"), "USD",
                                "sku-12345678"),
                        new PayPalItem("free sample item #2", 1, new BigDecimal("0.00"),
                                "USD", "sku-zero-price"),
                        new PayPalItem("sample item #3 with a longer name", 6, new BigDecimal("37.99"),
                                "USD", "sku-33333")
                };
        BigDecimal subtotal = PayPalItem.getItemTotal(items);
        BigDecimal shipping = new BigDecimal("7.21");
        BigDecimal tax = new BigDecimal("4.67");
        PayPalPaymentDetails paymentDetails = new PayPalPaymentDetails(shipping, subtotal, tax);
        BigDecimal amount = subtotal.add(shipping).add(tax);
        PayPalPayment payment = new PayPalPayment(amount, "USD", "sample item", paymentIntent);
        payment.items(items).paymentDetails(paymentDetails);

        //--- set other optional fields like invoice_number, custom field, and soft_descriptor
        payment.custom("This is text that will be associated with the payment that the app can use.");

        return payment;
    }

    /*
     * Add app-provided shipping address to payment
     */
    private void addAppProvidedShippingAddress(PayPalPayment paypalPayment) {
        ShippingAddress shippingAddress =
                new ShippingAddress().recipientName("Mom Parker").line1("52 North Main St.")
                        .city("Austin").state("TX").postalCode("78729").countryCode("US");
        paypalPayment.providedShippingAddress(shippingAddress);
    }

    /*
     * Enable retrieval of shipping addresses from buyer's PayPal account
     */
    private void enableShippingAddressRetrieval(PayPalPayment paypalPayment, boolean enable) {
        paypalPayment.enablePayPalShippingAddressesRetrieval(enable);
    }

    public void onFuturePaymentPressed(View pressed) {
        Intent intent = new Intent(PayWayActivity.this, PayPalFuturePaymentActivity.class);

        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        startActivityForResult(intent, REQUEST_CODE_FUTURE_PAYMENT);
    }

    public void onProfileSharingPressed(View pressed) {
        Intent intent = new Intent(PayWayActivity.this, PayPalProfileSharingActivity.class);

        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        intent.putExtra(PayPalProfileSharingActivity.EXTRA_REQUESTED_SCOPES, getOauthScopes());

        startActivityForResult(intent, REQUEST_CODE_PROFILE_SHARING);
    }

    private PayPalOAuthScopes getOauthScopes() {
        /* create the set of required scopes
         * Note: see https://developer.paypal.com/docs/integration/direct/identity/attributes/ for mapping between the
         * attributes you select for this app in the PayPal developer portal and the scopes required here.
         */
        Set<String> scopes = new HashSet<String>(
                Arrays.asList(PayPalOAuthScopes.PAYPAL_SCOPE_EMAIL, PayPalOAuthScopes.PAYPAL_SCOPE_ADDRESS));
        return new PayPalOAuthScopes(scopes);
    }

    protected void displayResultText(String result) {
        //   ((TextView)findViewById(R.id.txtResult)).setText("Result : " + result);
        Toast.makeText(
                getApplicationContext(),
                result, Toast.LENGTH_LONG)
                .show();
    }

    //支付结果返回
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm =
                        data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        LogUtil.i(confirm.toJSONObject().toString(4));
                        LogUtil.i("************:" + confirm.getPayment().toJSONObject().toString(4));

                        /**
                         * or consent completion.
                         * See https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
                         * for more details.
                         *
                         * For sample mobile backend interactions, see
                         * https://github.com/paypal/rest-api-sdk-python/tree/master/samples/mobile_backend
                         */
                        GsonParse.PetWalkParse(confirm.toJSONObject().toString(4));

                        PaypalVerifyInfo verifyInfo = GsonParse.PaypalVerifyParse(confirm.toJSONObject().toString(4));
                        if (verifyInfo != null) {
                            //ToastUtil.show(PayWayActivity.this, "支付成功：id:" + verifyInfo.id);
                            LogUtil.i("ID:" + verifyInfo.id + ",state:" + verifyInfo.state + ",intent:" + verifyInfo.intent + ",time:" + verifyInfo.create_time);
                            saveOrder(strTrackerNo, currency_unit, orderPackageId, verifyInfo.id);
                        } else {
                            LogUtil.i("verifyInfo is null");
                            // ToastUtil.show(PayWayActivity.this, "支付失败：");
                            ToastUtil.show(PayWayActivity.this, R.string.order_pay_error);
                        }


                        // displayResultText("PaymentConfirmation info received from PayPal");


                    } catch (JSONException e) {
                        LogUtil.i("an extremely unlikely failure occurred: ", e);

                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                LogUtil.i("The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                LogUtil.i("An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        } else if (requestCode == REQUEST_CODE_FUTURE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PayPalAuthorization auth =
                        data.getParcelableExtra(PayPalFuturePaymentActivity.EXTRA_RESULT_AUTHORIZATION);
                if (auth != null) {
                    try {
                        LogUtil.i(auth.toJSONObject().toString(4));
                        String authorization_code = auth.getAuthorizationCode();
                        LogUtil.i(authorization_code);
                        sendAuthorizationToServer(auth);
                        displayResultText("Future Payment code received from PayPal");

                    } catch (JSONException e) {
                        LogUtil.i("an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                LogUtil.i("The user canceled.");
            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
                LogUtil.i("Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
            }
        } else if (requestCode == REQUEST_CODE_PROFILE_SHARING) {
            if (resultCode == Activity.RESULT_OK) {
                PayPalAuthorization auth =
                        data.getParcelableExtra(PayPalProfileSharingActivity.EXTRA_RESULT_AUTHORIZATION);
                if (auth != null) {
                    try {
                        LogUtil.i(auth.toJSONObject().toString(4));
                        String authorization_code = auth.getAuthorizationCode();

                        LogUtil.i(authorization_code);
                        sendAuthorizationToServer(auth);
                        displayResultText("Profile Sharing code received from PayPal");

                    } catch (JSONException e) {
                        LogUtil.i("an extremely unlikely failure occurred: ", e);

                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                LogUtil.i("The user canceled.");
            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
                LogUtil.i("Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
            }
        }
    }

    private void sendAuthorizationToServer(PayPalAuthorization authorization) {

        /**
         * exchange the authorization code for OAuth access and refresh tokens.
         *
         * Your server must then store these tokens, so that your server code
         * can execute payments for this user in the future.
         *
         * A more complete example that includes the required app-server to
         * PayPal-server integration is available from
         * https://github.com/paypal/rest-api-sdk-python/tree/master/samples/mobile_backend
         */

    }

    public void onFuturePaymentPurchasePressed(View pressed) {
        // Get the Client Metadata ID from the SDK
        String metadataId = PayPalConfiguration.getClientMetadataId(this);
        LogUtil.i("Client Metadata ID: " + metadataId);
        // PayPal...
        displayResultText("Client Metadata Id received from SDK");
    }

    @Override
    public void onDestroy() {
        // Stop service when done
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    @Override
    public void getListPositon(int position) {


    }

    /**
     * 获取订单价格信息
     *
     * @param strTrackerNo
     */
    private void getOrderPackage(String strTrackerNo) {
        String url = UserUtil.getServerUrl(this);

        RequestParams params = HttpParams.getOrderPackage(strTrackerNo);

        requestHandle = HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.showNoCanceled(PayWayActivity.this,
                                null, PayWayActivity.this);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(
                                response));
                        if (obj == null)
                            return;
                        if (obj.code == 0) {
                            ToastUtil.show(PayWayActivity.this, obj.what);
                            OrderPackageInfo mOrderPackageInfo = GsonParse.getResponseData(new String(response), OrderPackageInfo.class);
                            //OrderPackageInfo mOrderPackageInfo = GsonParse.getOrderPackageInfoPrase(new String(response));
                            successfulAccessData(mOrderPackageInfo);
                        } else {
                            ToastUtil.show(PayWayActivity.this, obj.what);
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse,
                                throwable);
                        ToastUtil.show(PayWayActivity.this,
                                R.string.net_exception);
                        ProgressDialogUtil.dismiss();
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ProgressDialogUtil.dismiss();
                    }
                });
    }

    private void saveOrder(String strTrackerNo, String currencyUnit, String orderPackageId, String tradeNo) {
        String url = UserUtil.getServerUrl(this);
        RequestParams params = HttpParams.saveOrder(strTrackerNo, 2, tv_money, currencyUnit, orderPackageId, tradeNo);

        requestHandle = HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.showNoCanceled(PayWayActivity.this,
                                null, PayWayActivity.this);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(
                                response));
                        if (obj == null)
                            return;
                        if (obj.code == 0) {
                            mCurTracker.one_month_expired = false;
                            mCurTracker.expiredTimeDe = false;
                            Constants.isPay = true;
                            UserUtil.saveCurrentTracker(PayWayActivity.this, mCurTracker);
                            UserUtil.saveTracker(PayWayActivity.this, mCurTracker);
                            finish();
                        }
                        ToastUtil.show(PayWayActivity.this, obj.what);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse,
                                throwable);
                        ToastUtil.show(PayWayActivity.this,
                                R.string.net_exception);
                        ProgressDialogUtil.dismiss();
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ProgressDialogUtil.dismiss();
                    }
                });
    }

    private void successfulAccessData(OrderPackageInfo mOrderPackageInfo) {
        LogUtil.i("222222222222");
        if (mOrderPackageInfo != null) {
            LogUtil.i("6666666");
            OrderPackageInfo.DeviceBean mDevice = mOrderPackageInfo.getDevice();
            if (mDevice != null) {
                if (!Utils.isEmpty(mDevice.getClientId())) {
                    CONFIG_CLIENT_ID = mDevice.getClientId();
                }
                LogUtil.i("CONFIG_CLIENT_ID：" + CONFIG_CLIENT_ID);
                config = new PayPalConfiguration()
                        .environment(CONFIG_ENVIRONMENT)
                        .clientId(CONFIG_CLIENT_ID);
                Intent intent = new Intent(this, PayPalService.class);
                intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
                startService(intent);
            }
            mPackageListBean = mOrderPackageInfo.getPackageList();

            if (mPackageListBean != null && mPackageListBean.size() > 0) {
                adapter.setData(mPackageListBean);
                tv_package_name = mPackageListBean.get(0).getName();
                tv_money = mPackageListBean.get(0).getServe_fee() + "";
                currency_unit = mPackageListBean.get(0).getCurrency_unit();
                if (!Utils.isEmpty(currency_unit)) {
                    currency_unit = currency_unit.toUpperCase();
                }
                orderPackageId = mPackageListBean.get(0).getOrderPackageId();
                mBtnConfirmPayment.setVisibility(View.VISIBLE);
            } else {
                LogUtil.i("续费没有数据");
            }
        } else {
            LogUtil.i("333333");
        }
    }


}
