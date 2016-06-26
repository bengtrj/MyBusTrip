package com.mybustrip.service.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.mybustrip.StringUtils;
import com.mybustrip.model.Identifiable;
import com.mybustrip.model.PublicStop;
import com.mybustrip.model.RealtimeRoute;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by bengthammarlund on 08/05/16.
 */
public class Repository extends SQLiteOpenHelper {

    private static final String LOG_TAG = Repository.class.getSimpleName();

    private static final String DB_NAME = "MyBusTrip.db";

    private static final int DB_VERSION = 1;

    private static final String DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";

    private static final String COLUMN_ID = "_id";

    public Repository(Context context) {
        super(context, DB_NAME, null, DB_VERSION, null);
//        printSQLDump();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        createBaseTables(db);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        createBaseTables(db);
    }

    private void printSQLDump() {
        final List<PublicStop> publicStops = this.listPublicStops(0L, 0L);
        int i = 0;
        for (PublicStop p : publicStops) {
            i++;
            Log.d("SQL_DUMP", "<item>" + createLine(p) + "</item>");
            if (i % 2500 == 1) {
                System.currentTimeMillis();
            }
        }
    }

    private String createLine(PublicStop p) {
        return "insert into " + PublicStopTable.TABLE_NAME + "(" +
                PublicStopTable.STOP_ID + ", " +
                PublicStopTable.NAME + ", " +
                PublicStopTable.ROUTES + ", " +
                PublicStopTable.LATITUDE + ", " +
                PublicStopTable.LONGITUDE +
                ") values (" +
                p.getStopId() + ", '" +
                p.getName() + "', '" +
                than(p).substring(0, than(p).length() -1).replaceAll(" ", "") + "', " +
                p.getLatitude() + ", " +
                p.getLongitude() +
                ");";
    }

    @NonNull
    private String than(PublicStop p) {
        return Arrays.toString(p.getRoutes()).substring(1);
    }

    private void createBaseTables(SQLiteDatabase db) {
        dropIndexes(db);
        dropTables(db);

        createTables(db);
        createIndexes(db);
    }

    private void createIndexes(SQLiteDatabase db) {
        db.execSQL("CREATE INDEX PUBLIC_STOP_LATITUDE ON PUBLIC_STOP (latitude)");
        db.execSQL("CREATE INDEX PUBLIC_STOP_LONGITUDE ON PUBLIC_STOP (longitude)");
    }

    private void createTables(SQLiteDatabase db) {
        db.execSQL(
                "   CREATE TABLE IF NOT EXISTS PUBLIC_STOP (    " +
                        "       _id Integer PRIMARY KEY,                " +
                        "       stop_id Integer UNIQUE NOT NULL,        " +
                        "       name varchar(100) NOT NULL,             " +
                        "       routes text NOT NULL,                   " +
                        "       latitude Real NOT NULL,                 " +
                        "       longitude Real NOT NULL)                ");

        db.execSQL(
                "   CREATE TABLE IF NOT EXISTS REALTIME_ROUTE (     " +
                        "       _id Integer PRIMARY KEY AUTOINCREMENT,      " +
                        "       stop_id Integer NOT NULL,                   " +
                        "       route varchar(10) NOT NULL,                 " +
                        "       origin varchar(100) NOT NULL,               " +
                        "       destination varchar(100) NOT NULL,          " +
                        "       direction varchar(100) NOT NULL,            " +
                        "       dueTime varchar(3) NOT NULL,                " +
                        "       operator varchar(5) NOT NULL)               ");
    }

    private void dropTables(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS PUBLIC_STOP");
        db.execSQL("DROP TABLE IF EXISTS REALTIME_ROUTE");
    }

    private void dropIndexes(SQLiteDatabase db) {
        db.execSQL("DROP INDEX IF EXISTS PUBLIC_STOP_LATITUDE");
        db.execSQL("DROP INDEX IF EXISTS PUBLIC_STOP_LONGITUDE");
    }

    public void save(final PublicStop publicStop) {
        ContentValues values = PublicStopTable.toContentValues(publicStop);
        publicStop.setId(
                getWritableDatabase().insert(PublicStopTable.TABLE_NAME, null, values)
        );
    }

    public List<PublicStop> listPublicStops(double latitude, double longitude) {
        return PublicStopTable.fromMultiple(
                getReadableDatabase().query(
                        PublicStopTable.TABLE_NAME,
                        PublicStopTable.allColumns,
//                " abs(latitude - ?) < 0.02 and abs(longitude - ?) < 0.02",
//                new String[] {
//                        BigDecimal.valueOf(latitude).toPlainString(),
//                        BigDecimal.valueOf(longitude).toPlainString()
//                },
                        null,
                        null,
                        null,
                        null,
                        PublicStopTable.LONGITUDE
                ));
    }

    private static final class PublicStopTable {
        private static final String TABLE_NAME = "PUBLIC_STOP";
        private static final String NAME = "name";
        private static final String ROUTES = "routes";
        private static final String STOP_ID = "stop_id";
        private static final String LATITUDE = "latitude";
        private static final String LONGITUDE = "longitude";

        private static final String[] allColumns = {COLUMN_ID, NAME, ROUTES, STOP_ID, LATITUDE, LONGITUDE};

        static ContentValues toContentValues(PublicStop publicStop) {
            final ContentValues values = getIdentificableContentValues(publicStop);

            values.put(NAME, publicStop.getName());
            values.put(ROUTES, _internalArrayToString(publicStop.getRoutes()));
            values.put(STOP_ID, publicStop.getName());
            values.put(LATITUDE, publicStop.getLatitude());
            values.put(LONGITUDE, publicStop.getLongitude());

            return values;
        }

        static PublicStop from(Cursor cursor) {
            final PublicStop publicStop = new PublicStop();
            publicStop.setId(cursor.getLong(0));
            publicStop.setName(cursor.getString(1));
            publicStop.setRoutes(_internalStringToArray(cursor.getString(2)));
            publicStop.setStopId(cursor.getString(3));
            publicStop.setLatitude(cursor.getDouble(4));
            publicStop.setLongitude(cursor.getDouble(5));
            return publicStop;
        }

        static List<PublicStop> fromMultiple(Cursor cursor) {
            List<PublicStop> publicStops = new ArrayList<>(cursor.getCount());

            if (cursor.isBeforeFirst()) {
                while (cursor.moveToNext()) {
                    publicStops.add(from(cursor));
                }
            }

            return publicStops;
        }
    }

    private static final class RouteTable {

        private static final String TABLE_NAME = "REALTIME_ROUTE";
        private static final String STOP_ID = "stop_id";
        private static final String ROUTE = "route";
        private static final String ORIGIN = "origin";
        private static final String DESTINATION = "destination";
        private static final String DIRECTION = "direction";
        private static final String OPERATOR = "operator";
        private static final String DUE_TIME = "dueTime";
        private static final String LAST_UPDATED = "name";
        private static final String[] allColumns = {COLUMN_ID, STOP_ID, ROUTE, ORIGIN, DESTINATION, DIRECTION, OPERATOR, DUE_TIME, LAST_UPDATED};

        static ContentValues toContentValues(RealtimeRoute realtimeRoute) {
            final ContentValues values = getIdentificableContentValues(realtimeRoute);

            values.put(STOP_ID, realtimeRoute.getStopId());
            values.put(ROUTE, realtimeRoute.getRoute());
            values.put(ORIGIN, realtimeRoute.getOrigin());
            values.put(DESTINATION, realtimeRoute.getDestination());
            values.put(DIRECTION, realtimeRoute.getDirection());
            values.put(OPERATOR, realtimeRoute.getOperator());
            values.put(DUE_TIME, realtimeRoute.getDueTime());
            values.put(LAST_UPDATED, stringFromDate(realtimeRoute.getLastUpdated()));

            return values;
        }

        static RealtimeRoute from(Cursor cursor) {
            final RealtimeRoute realtimeRoute = new RealtimeRoute();
            realtimeRoute.setId(cursor.getLong(1));
            realtimeRoute.setStopId(cursor.getString(2));
            realtimeRoute.setRoute(cursor.getString(3));
            realtimeRoute.setOrigin(cursor.getString(4));
            realtimeRoute.setDestination(cursor.getString(5));
            realtimeRoute.setDirection(cursor.getString(6));
            realtimeRoute.setOperator(cursor.getString(7));
            realtimeRoute.setDueTime(cursor.getString(8));
            realtimeRoute.setLastUpdated(dateFromString(cursor.getString(9)));
            return realtimeRoute;
        }

    }

    private static String stringFromDate(Date date) {
        return new SimpleDateFormat(DATE_FORMAT).format(date);
    }

    private static Date dateFromString(String date) {
        Date result = null;
        if (StringUtils.isNotBlank(date)) {
            try {
                result = new SimpleDateFormat(DATE_FORMAT).parse(date);
            } catch (ParseException e) {
                Log.e(LOG_TAG, "Error parsing date from database: " + date, e);
            }
        }

        return result;
    }

    @NonNull
    private static ContentValues getIdentificableContentValues(Identifiable identifiable) {
        final ContentValues values = new ContentValues();
        final Long id = identifiable.getId();
        if (id != null) {
            values.put(COLUMN_ID, id);
        }
        return values;
    }

    private static String _internalArrayToString(String[] strings) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < strings.length; ) {
            sb.append(strings[i]);
            if (++i < strings.length) {
                sb.append("|");
            }
        }
        return sb.toString();
    }

    private static String[] _internalStringToArray(final String arrayStr) {
        return arrayStr.split(Pattern.quote("|"));
    }

}
