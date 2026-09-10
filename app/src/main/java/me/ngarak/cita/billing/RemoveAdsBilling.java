package me.ngarak.cita.billing;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.PendingPurchasesParams;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;

import java.util.Collections;
import java.util.List;

import me.ngarak.cita.BuildConfig;
import me.ngarak.cita.CitaPlus;

/**
 * One-time Remove Ads IAP. Gated by {@link BuildConfig#ENABLE_REMOVE_ADS_IAP}
 * until Weekly Shared Cards / share rate justifies turning it on in Play Console.
 */
public final class RemoveAdsBilling {
    public static final String PRODUCT_ID = "cita_remove_ads";
    private static final String TAG = "RemoveAdsBilling";

    public interface Listener {
        void onReady(boolean available);

        void onPurchased();

        void onError(String message);
    }

    private final Context app;
    private final CitaPlus plus;
    @Nullable
    private BillingClient client;
    @Nullable
    private ProductDetails productDetails;
    @Nullable
    private Listener listener;

    public RemoveAdsBilling(Context context) {
        app = context.getApplicationContext();
        plus = new CitaPlus(app);
    }

    public static boolean isEnabled() {
        return BuildConfig.ENABLE_REMOVE_ADS_IAP;
    }

    public void start(@Nullable Listener listener) {
        this.listener = listener;
        if (!isEnabled()) {
            if (listener != null) listener.onReady(false);
            return;
        }
        if (plus.adsRemoved()) {
            if (listener != null) {
                listener.onReady(false);
                listener.onPurchased();
            }
            return;
        }
        client = BillingClient.newBuilder(app)
                .setListener((result, purchases) -> handlePurchases(purchases))
                .enablePendingPurchases(
                        PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
                .build();
        client.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
                    notifyError("Billing unavailable");
                    return;
                }
                queryProduct();
                queryOwned();
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Caller may retry.
            }
        });
    }

    public void launchPurchase(Activity activity) {
        if (!isEnabled() || client == null || productDetails == null) {
            notifyError("Remove Ads is not available yet");
            return;
        }
        ProductDetails.OneTimePurchaseOfferDetails offer =
                productDetails.getOneTimePurchaseOfferDetails();
        if (offer == null) {
            notifyError("Product misconfigured");
            return;
        }
        BillingFlowParams.ProductDetailsParams params =
                BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetails)
                        .build();
        BillingFlowParams flow = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(Collections.singletonList(params))
                .build();
        client.launchBillingFlow(activity, flow);
    }

    public void destroy() {
        listener = null;
        if (client != null) {
            client.endConnection();
            client = null;
        }
    }

    private void queryProduct() {
        if (client == null) return;
        QueryProductDetailsParams.Product product = QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PRODUCT_ID)
                .setProductType(BillingClient.ProductType.INAPP)
                .build();
        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(Collections.singletonList(product))
                .build();
        client.queryProductDetailsAsync(params, (result, list) -> {
            if (result.getResponseCode() == BillingClient.BillingResponseCode.OK
                    && list != null && !list.isEmpty()) {
                productDetails = list.get(0);
                if (listener != null) listener.onReady(true);
            } else {
                Log.w(TAG, "product missing code=" + result.getResponseCode());
                if (listener != null) listener.onReady(false);
            }
        });
    }

    private void queryOwned() {
        if (client == null) return;
        client.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder()
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build(),
                (result, purchases) -> {
                    if (result.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        handlePurchases(purchases);
                    }
                });
    }

    private void handlePurchases(@Nullable List<Purchase> purchases) {
        if (purchases == null) return;
        for (Purchase p : purchases) {
            if (p.getProducts().contains(PRODUCT_ID)
                    && p.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                grant(p);
            }
        }
    }

    private void grant(Purchase purchase) {
        plus.setAdsRemoved(true);
        if (client != null && !purchase.isAcknowledged()) {
            AcknowledgePurchaseParams params = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.getPurchaseToken())
                    .build();
            client.acknowledgePurchase(params, result -> {
                if (result.getResponseCode() != BillingClient.BillingResponseCode.OK) {
                    Log.w(TAG, "ack failed " + result.getDebugMessage());
                }
            });
        }
        if (listener != null) listener.onPurchased();
    }

    private void notifyError(String message) {
        Log.w(TAG, message);
        if (listener != null) listener.onError(message);
    }
}
