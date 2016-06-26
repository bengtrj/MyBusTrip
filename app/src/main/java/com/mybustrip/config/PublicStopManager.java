package com.mybustrip.config;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.inject.Inject;
import com.mybustrip.CollectionUtil;
import com.mybustrip.R;
import com.mybustrip.model.PublicStop;
import com.mybustrip.model.PublicStopMarkerManager;
import com.mybustrip.model.RealtimeRoute;
import com.mybustrip.ui.MarkerSnippetFormatter;
import com.mybustrip.ui.UiUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.mybustrip.model.PublicStopMarkerManager.MarkerStatus.*;

/**
 * Created by bengthammarlund on 22/05/16.
 */
public class PublicStopManager {

    public static final String LOG_TAG = PublicStopManager.class.getSimpleName();

    @Inject
    private PublicStopMarkerManager markerManager;

    @Inject
    private MarkerSnippetFormatter snippetFormatter;

    private GoogleMap googleMap;

    public void addPublicStop(final PublicStop publicStop) {
        final LatLng position = new LatLng(publicStop.getLatitude(), publicStop.getLongitude());
        try {
            Marker marker = markerManager.getMarker(publicStop);
            if (marker == null) {
                marker = googleMap.addMarker(buildMarkerOptions(publicStop, position));
                markerManager.put(publicStop, marker);
            }

            Log.d(LOG_TAG, "Marker " + marker.getId() + " do publicStop: " + publicStop + " / " + position);

        } catch (Exception e) {
            Log.w(LOG_TAG, "Erro ao criar o Marker stopId: " + publicStop.getStopId() + " / " + position, e);
        }
    }

    public void addRealtimeInfo(final String stopId, final List<RealtimeRoute> realtimeRoutes) {
        final PublicStop publicStop = markerManager.getPublicStopByStopId(stopId);
        if (publicStop != null) {
            publicStop.setRealtimeRoutes(realtimeRoutes);
            final Marker marker = markerManager.getMarker(publicStop);
            final PublicStopMarkerManager.MarkerStatus status = CollectionUtil.isEmpty(realtimeRoutes) ? REALTIME_DATA_UNAVAILABLE : REALTIME_DATA_AVAILABLE;
            markerManager.updateMarkerStatus(marker, status);
            updateMarkerUi(marker);
        }
    }

    private List<RealtimeRoute> filterRealtimeInfo(final List<RealtimeRoute> realtimeRoutes) {
        final List<RealtimeRoute> filteredRoutes = new ArrayList<>(realtimeRoutes.size());
        final Set<String> busStopNumber = new HashSet<>(realtimeRoutes.size());
        Collections.sort(realtimeRoutes);

        for (RealtimeRoute route : realtimeRoutes) {
            if (busStopNumber.add(route.getRoute())) {
                filteredRoutes.add(route);
            }
        }

        return filteredRoutes;
    }

    public void initialize(final GoogleMap googleMap, final Context context) {

        this.googleMap = googleMap;

        googleMap.setBuildingsEnabled(true);
        googleMap.setIndoorEnabled(false);
        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setMapToolbarEnabled(false);

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                updatePublicStopRealTimeInfo(marker, context);
                return false;
            }
        });

        googleMap.setBuildingsEnabled(true);

        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                final PublicStop publicStop = markerManager.getPublicStopByMarkerId(marker.getId());

                final LinearLayout root = new LinearLayout(context);
                root.setOrientation(LinearLayout.VERTICAL);
                root.addView(getTitle(publicStop));

                switch (markerManager.getMarkerStatus(marker)) {

                    case CREATED:
                        addPlainSnippet(root, publicStop, snippetFormatter.getPlainSnippet(publicStop));
                        break;
                    case OBTAINING_REALTIME_DATA:
                        addPlainSnippet(root, publicStop, snippetFormatter.getPlainSnippet(publicStop));
                        addPlainSnippet(root, publicStop, context.getText(R.string.obtaining_realtime_data));
                        break;
                    case REALTIME_DATA_UNAVAILABLE:
                        addPlainSnippet(root, publicStop, snippetFormatter.getPlainSnippet(publicStop));
                        addPlainSnippet(root, publicStop, context.getText(R.string.realtime_data_unavailable));
                        break;
                    case REALTIME_DATA_AVAILABLE:
                        final TableLayout table = new TableLayout(context);
                        for (RealtimeRoute route : filterRealtimeInfo(publicStop.getRealtimeRoutes())) {
                            table.addView(addPublicStopRealtimeInfoRow(table, route));
                        }
                        root.addView(table);
                        break;

                }

                return root;
            }

            private TableRow addPublicStopRealtimeInfoRow(TableLayout table, RealtimeRoute route) {
                final TableRow row = new TableRow(context);
                row.setPadding(0, 0, 0, 5);
                row.addView(createRouteIdCell(route));
                row.addView(createDirectionCell(route));
                row.addView(createDueTimeCell(route));
                return row;
            }

            @NonNull
            private TextView createRouteIdCell(RealtimeRoute route) {
                final TextView routeId = getTextRow(R.color.mapsBlue, R.color.white);
                routeId.setText(route.getRoute());
                return routeId;
            }

            @NonNull
            private TextView createDirectionCell(RealtimeRoute route) {
                final TextView directionCell = getTextRow(R.color.mapsMarfil, R.color.darkGrey);
                directionCell.setText(route.getDestination());
                return directionCell;
            }

            @NonNull
            private TextView createDueTimeCell(RealtimeRoute route) {
                final TextView realtimeInfo = getTextRow(R.color.mapsBrown, R.color.white);
                boolean isDue = snippetFormatter.isDue(route);
                if (isDue) {
                    realtimeInfo.setTypeface(null, Typeface.BOLD_ITALIC);
                }
                realtimeInfo.setText(" " + (isDue ? "Due" : route.getDueTime() + " min"));
                realtimeInfo.setGravity(Gravity.RIGHT);
                return realtimeInfo;
            }

            @NonNull
            private TextView getTextRow(@ColorRes final int backgroundColor, @ColorRes final int textColor) {
                final TextView routeId = new TextView(context);
                routeId.setTypeface(null, Typeface.BOLD);
                routeId.setGravity(Gravity.LEFT);
                routeId.setBackgroundColor(ContextCompat.getColor(context, backgroundColor));
                routeId.setTextColor(ContextCompat.getColor(context, textColor));
                int lateralPadding = UiUtils.dpToPx(5);
                routeId.setPadding(lateralPadding, 0, lateralPadding, 0);
                return routeId;
            }

            private void addPlainSnippet(LinearLayout root, PublicStop publicStop, CharSequence text) {
                final TextView snippet = new TextView(context);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(text);
                root.addView(snippet);
            }

            @NonNull
            private TextView getTitle(PublicStop publicStop) {
                final TextView title = new TextView(context);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(publicStop.getStopId());
                return title;
            }
        });
    }

    @NonNull
    private MarkerOptions buildMarkerOptions(final PublicStop publicStop, final LatLng position) {
        return new MarkerOptions()
                .position(position)
                .title(publicStop.getStopId())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bus_71))
                .infoWindowAnchor(0.5f, 0.5f);
    }

    private void updatePublicStopRealTimeInfo(final Marker marker, final Context context) {

        PublicStop publicStop = markerManager.getPublicStopByMarkerId(marker.getId());
        markerManager.updateMarkerStatus(marker, OBTAINING_REALTIME_DATA);
        updateMarkerUi(marker);

        Intent intent = new Intent(IntentFilters.OBTAIN_REALTIME_INFO);
        intent.setPackage(context.getPackageName());
        intent.putExtra(Constants.STOP_ID, publicStop.getStopId());
        context.startService(intent);
    }

    private void updateMarkerUi(final Marker marker) {
        if (marker != null) {
            marker.hideInfoWindow();
            marker.showInfoWindow();
            Log.d(LOG_TAG, "Marker coordinates " + marker.getPosition() + " updated");
        }
    }

}
