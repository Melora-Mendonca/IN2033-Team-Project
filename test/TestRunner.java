import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.engine.discovery.DiscoverySelectors;

import java.io.PrintWriter;

public class TestRunner {
    public static void main(String[] args) {
        SummaryGeneratingListener listener = new SummaryGeneratingListener();

        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(
                        DiscoverySelectors.selectClass("IPOS.SA.ORD.Model.OrderItemTest"),
                        DiscoverySelectors.selectClass("IPOS.SA.ORD.Model.OrderTest"),
                        DiscoverySelectors.selectClass("IPOS.SA.ORD.Model.InvoiceTest"),
                        DiscoverySelectors.selectClass("IPOS.SA.ORD.OrderStatusTest"),
                        DiscoverySelectors.selectClass("IPOS.SA.ORD.CatalogueItemTest")
                )
                .build();

        Launcher launcher = LauncherFactory.create();
        launcher.discover(request);
        launcher.execute(request, listener);

        listener.getSummary().printFailuresTo(new PrintWriter(System.out, true));

        long passed = listener.getSummary().getTestsSucceededCount();
        long failed = listener.getSummary().getTestsFailedCount();
        long total  = listener.getSummary().getTestsStartedCount();

        System.out.println("\n========================================");
        System.out.println("  Tests run: " + total);
        System.out.println("  Passed:    " + passed);
        System.out.println("  Failed:    " + failed);
        System.out.println("========================================");

        if (failed > 0) System.exit(1);
    }
}
