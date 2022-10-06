
package git.shin.animevsub;
import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import com.getcapacitor.Bridge;
import com.getcapacitor.BridgeWebViewClient;

public class EnableHttpsSelfSigned {
  public static void enable(Bridge bridge) {
    bridge.getWebView().setWebViewClient(new BridgeWebViewClient(bridge) {
      @Override
      public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
        handler.proceed();
      }
    });
  }
}