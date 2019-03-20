package com.a65apps.mapkitclustering;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.a65apps.clustering.core.Cluster;
import com.a65apps.clustering.core.ClustersDiff;
import com.a65apps.clustering.core.DefaultCluster;
import com.a65apps.clustering.core.VisibleRect;
import com.a65apps.clustering.core.algorithm.NonHierarchicalDistanceBasedAlgorithm;
import com.a65apps.clustering.core.view.ClusterRenderer;
import com.a65apps.clustering.yandex.YandexClusterManager;
import com.a65apps.clustering.yandex.extention.PointExtKt;
import com.a65apps.clustering.yandex.view.ClusterPinProvider;
import com.a65apps.clustering.yandex.view.TapListener;
import com.a65apps.clustering.yandex.view.YandexClusterRenderer;
import com.a65apps.clustering.yandex.view.YandexRenderConfig;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.InputListener;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.mapview.MapView;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SampleJavaActivity extends AppCompatActivity {

    private Set<Cluster> testMarkers = new HashSet<>();
    private YandexClusterManager clusterManager;
    private Toast toast;
    private final InputListener inputListener = new InputListener() {
        @Override
        public void onMapTap(@NonNull Map map,
                             @NonNull Point point) {
            showToast(point.toString());
        }

        @Override
        public void onMapLongTap(@NonNull Map map,
                                 @NonNull Point point) {
            testMarkers.add(new DefaultCluster(PointExtKt.toLatLng(point), null));
            clusterManager.addItem(new DefaultCluster(PointExtKt.toLatLng(point), null));
            showToast(point.toString());
        }
    };
    private Cluster selectedCluster;
    private final TapListener tapListener = new TapListener() {
        @Override
        public void clusterTapped(@NotNull Cluster cluster,
                                  @NotNull PlacemarkMapObject mapObject) {
            showToast(cluster.toString());
            selectedCluster = (cluster.isCluster()) ? cluster.items().iterator().next() : cluster;
        }
    };

    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MapKitFactory.setApiKey(BuildConfig.YANDEX_MAP_KIT_KEY);
        MapKitFactory.initialize(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Java version");

        ClusterPinProvider clusterPinProvider = new CustomPinProvider(this);
        mapView = findViewById(R.id.mapView);
        Map map = mapView.getMap();
        YandexRenderConfig renderConfig = new YandexRenderConfig();
        ClusterRenderer<ClustersDiff, YandexRenderConfig> clusterRenderer =
                new YandexClusterRenderer(map, clusterPinProvider, renderConfig, tapListener,
                        "RENDER_LAYER");
        clusterManager = new YandexClusterManager(clusterRenderer,
                new NonHierarchicalDistanceBasedAlgorithm(new CustomClusterProvider()),
                new VisibleRect(
                        PointExtKt.toLatLng(map.getVisibleRegion().getTopLeft()),
                        PointExtKt.toLatLng(map.getVisibleRegion().getBottomRight())));
        map.addCameraListener(clusterManager);
        map.addInputListener(inputListener);
        map.move(TestData.INSTANCE.getCAMERA_POSITION());
    }

    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        MapKitFactory.getInstance().onStop();
        mapView.onStop();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_marker: {
                addTestPoint();
                break;
            }
            case R.id.remove_marker: {
                removeTestPoint();
                break;
            }
            case R.id.add_markers: {
                addTestPoints(10);
                break;
            }
            case R.id.remove_markers: {
                removeTestPoints();
                break;
            }
            case R.id.set_markers: {
                setTestPoints();
                break;
            }
            case R.id.clear_markers: {
                clearTestPoints();
                break;
            }
            case R.id.switch_activity: {
                switchActivity();
                break;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
        return true;
    }

    private void switchActivity() {
        Intent intent = new Intent(this, SampleKotlinActivity.class);
        startActivity(intent);
        finish();
    }

    private void setTestPoints() {
        Set<Cluster> markers = new HashSet<>();
        clusterManager.clearItems();
        for (Point point : TestData.INSTANCE.getPOINTS_LIST()) {
            markers.add(new DefaultCluster(PointExtKt.toLatLng(point), null));
        }
        clusterManager.setItems(markers);
    }

    private void clearTestPoints() {
        clusterManager.clearItems();
    }

    private void addTestPoint() {
        Point point = TestData.INSTANCE.randomPoint();
        Cluster marker = new DefaultCluster(PointExtKt.toLatLng(point), null);
        testMarkers.add(marker);
        clusterManager.addItem(marker);
    }

    private void removeTestPoint() {
        if (selectedCluster != null) {
            clusterManager.removeItem(selectedCluster);
        }
    }

    private void addTestPoints(int count) {
        for (int i = 0; i < count; i++) {
            Cluster marker =
                    new DefaultCluster(PointExtKt.toLatLng(TestData.INSTANCE.randomPoint()), null);
            testMarkers.add(marker);
        }
        clusterManager.addItems(testMarkers);
    }

    private void removeTestPoints() {
        clusterManager.removeItems(testMarkers);
        testMarkers.clear();
    }

    private void showToast(@NonNull String text) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.show();
    }
}