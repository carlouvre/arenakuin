package com.example.arenakuin.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ArenaKuIn.db";
    private static final int DATABASE_VERSION = 4;

    // Table Users
    private static final String TABLE_USERS = "users";
    private static final String COL_USER_ID = "user_id";
    private static final String COL_USER_NAME = "name";
    private static final String COL_USER_EMAIL = "email";
    private static final String COL_USER_PASSWORD = "password";
    private static final String COL_USER_PHONE = "phone";
    private static final String COL_USER_ROLE = "role";
    private static final String COL_USER_POINTS = "points";
    private static final String COL_USER_MEMBERSHIP = "membership_type";
    private static final String COL_USER_CREATED_AT = "created_at";

    // Table Venues
    private static final String TABLE_VENUES = "venues";
    private static final String COL_VENUE_ID = "venue_id";
    private static final String COL_VENUE_NAME = "venue_name";
    private static final String COL_VENUE_TYPE = "venue_type";
    private static final String COL_VENUE_LOCATION = "location";
    private static final String COL_VENUE_PRICE = "price_per_hour";
    private static final String COL_VENUE_FACILITIES = "facilities";
    private static final String COL_VENUE_IMAGE = "image_url";
    private static final String COL_VENUE_RATING = "rating";
    private static final String COL_VENUE_OPEN_TIME = "open_time";
    private static final String COL_VENUE_CLOSE_TIME = "close_time";
    private static final String COL_VENUE_IS_ACTIVE = "is_active";

    // Table Bookings
    private static final String TABLE_BOOKINGS = "bookings";
    private static final String COL_BOOKING_ID = "booking_id";
    private static final String COL_BOOKING_USER_ID = "user_id";
    private static final String COL_BOOKING_VENUE_ID = "venue_id";
    private static final String COL_BOOKING_DATE = "booking_date";
    private static final String COL_BOOKING_START_TIME = "start_time";
    private static final String COL_BOOKING_END_TIME = "end_time";
    private static final String COL_BOOKING_TOTAL_PRICE = "total_price";
    private static final String COL_BOOKING_STATUS = "status";
    private static final String COL_BOOKING_PAYMENT_STATUS = "payment_status";
    private static final String COL_BOOKING_PAYMENT_METHOD = "payment_method";
    private static final String COL_BOOKING_TYPE = "booking_type";
    private static final String COL_BOOKING_NOTES = "notes";
    private static final String COL_BOOKING_CREATED_AT = "created_at";
    private static final String COL_BOOKING_CREATED_BY = "created_by";

    // Table Favorites
    private static final String TABLE_FAVORITES = "favorites";
    private static final String COL_FAV_ID = "fav_id";
    private static final String COL_FAV_USER_ID = "user_id";
    private static final String COL_FAV_VENUE_ID = "venue_id";

    // Table Reviews
    private static final String TABLE_REVIEWS = "reviews";
    private static final String COL_REVIEW_ID = "review_id";
    private static final String COL_REVIEW_USER_ID = "user_id";
    private static final String COL_REVIEW_VENUE_ID = "venue_id";
    private static final String COL_REVIEW_RATING = "rating";
    private static final String COL_REVIEW_COMMENT = "comment";
    private static final String COL_REVIEW_DATE = "review_date";

    // Table Payment Confirmations
    private static final String TABLE_PAYMENT_CONFIRMATIONS = "payment_confirmations";
    private static final String COL_PAYMENT_ID = "payment_id";
    private static final String COL_PAYMENT_BOOKING_ID = "booking_id";
    private static final String COL_PAYMENT_AMOUNT = "amount";
    private static final String COL_PAYMENT_PROOF = "payment_proof";
    private static final String COL_PAYMENT_CONFIRMED_BY = "confirmed_by";
    private static final String COL_PAYMENT_CONFIRMED_AT = "confirmed_at";
    private static final String COL_PAYMENT_STATUS = "status";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Tables
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_NAME + " TEXT NOT NULL, " +
                COL_USER_EMAIL + " TEXT UNIQUE NOT NULL, " +
                COL_USER_PASSWORD + " TEXT NOT NULL, " +
                COL_USER_PHONE + " TEXT, " +
                COL_USER_ROLE + " TEXT DEFAULT 'customer', " +
                COL_USER_POINTS + " INTEGER DEFAULT 0, " +
                COL_USER_MEMBERSHIP + " TEXT DEFAULT 'Regular', " +
                COL_USER_CREATED_AT + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_VENUES + " (" +
                COL_VENUE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_VENUE_NAME + " TEXT NOT NULL, " +
                COL_VENUE_TYPE + " TEXT NOT NULL, " +
                COL_VENUE_LOCATION + " TEXT NOT NULL, " +
                COL_VENUE_PRICE + " REAL NOT NULL, " +
                COL_VENUE_FACILITIES + " TEXT, " +
                COL_VENUE_IMAGE + " TEXT, " +
                COL_VENUE_RATING + " REAL DEFAULT 0.0, " +
                COL_VENUE_OPEN_TIME + " TEXT, " +
                COL_VENUE_CLOSE_TIME + " TEXT, " +
                COL_VENUE_IS_ACTIVE + " INTEGER DEFAULT 1)");

        db.execSQL("CREATE TABLE " + TABLE_BOOKINGS + " (" +
                COL_BOOKING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_BOOKING_USER_ID + " INTEGER NOT NULL, " +
                COL_BOOKING_VENUE_ID + " INTEGER NOT NULL, " +
                COL_BOOKING_DATE + " TEXT NOT NULL, " +
                COL_BOOKING_START_TIME + " TEXT NOT NULL, " +
                COL_BOOKING_END_TIME + " TEXT NOT NULL, " +
                COL_BOOKING_TOTAL_PRICE + " REAL NOT NULL, " +
                COL_BOOKING_STATUS + " TEXT DEFAULT 'Pending', " +
                COL_BOOKING_PAYMENT_STATUS + " TEXT DEFAULT 'Unpaid', " +
                COL_BOOKING_PAYMENT_METHOD + " TEXT, " +
                COL_BOOKING_TYPE + " TEXT DEFAULT 'Online', " +
                COL_BOOKING_NOTES + " TEXT, " +
                COL_BOOKING_CREATED_AT + " TEXT, " +
                COL_BOOKING_CREATED_BY + " INTEGER, " +
                "FOREIGN KEY(" + COL_BOOKING_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + "), " +
                "FOREIGN KEY(" + COL_BOOKING_VENUE_ID + ") REFERENCES " + TABLE_VENUES + "(" + COL_VENUE_ID + "))");

        db.execSQL("CREATE TABLE " + TABLE_FAVORITES + " (" +
                COL_FAV_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_FAV_USER_ID + " INTEGER NOT NULL, " +
                COL_FAV_VENUE_ID + " INTEGER NOT NULL, " +
                "FOREIGN KEY(" + COL_FAV_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + "), " +
                "FOREIGN KEY(" + COL_FAV_VENUE_ID + ") REFERENCES " + TABLE_VENUES + "(" + COL_VENUE_ID + "))");

        db.execSQL("CREATE TABLE " + TABLE_REVIEWS + " (" +
                COL_REVIEW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_REVIEW_USER_ID + " INTEGER NOT NULL, " +
                COL_REVIEW_VENUE_ID + " INTEGER NOT NULL, " +
                COL_REVIEW_RATING + " REAL NOT NULL, " +
                COL_REVIEW_COMMENT + " TEXT, " +
                COL_REVIEW_DATE + " TEXT, " +
                "FOREIGN KEY(" + COL_REVIEW_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + "), " +
                "FOREIGN KEY(" + COL_REVIEW_VENUE_ID + ") REFERENCES " + TABLE_VENUES + "(" + COL_VENUE_ID + "))");

        db.execSQL("CREATE TABLE " + TABLE_PAYMENT_CONFIRMATIONS + " (" +
                COL_PAYMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PAYMENT_BOOKING_ID + " INTEGER NOT NULL, " +
                COL_PAYMENT_AMOUNT + " REAL NOT NULL, " +
                COL_PAYMENT_PROOF + " TEXT, " +
                COL_PAYMENT_CONFIRMED_BY + " INTEGER, " +
                COL_PAYMENT_CONFIRMED_AT + " TEXT, " +
                COL_PAYMENT_STATUS + " TEXT DEFAULT 'Pending', " +
                "FOREIGN KEY(" + COL_PAYMENT_BOOKING_ID + ") REFERENCES " + TABLE_BOOKINGS + "(" + COL_BOOKING_ID + "))");

        insertInitialData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PAYMENT_CONFIRMATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VENUES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REVIEWS);
        onCreate(db);
    }

    // --- SEEDING DATA ---
    private void insertInitialData(SQLiteDatabase db) {
        // Users Default
        ContentValues admin = new ContentValues();
        admin.put(COL_USER_NAME, "Admin ArenaKu");
        admin.put(COL_USER_EMAIL, "admin@arenakuin.com");
        admin.put(COL_USER_PASSWORD, "admin123");
        admin.put(COL_USER_PHONE, "081234567890");
        admin.put(COL_USER_ROLE, "admin");
        admin.put(COL_USER_CREATED_AT, getCurrentDateTime());
        db.insert(TABLE_USERS, null, admin);

        ContentValues receptionist = new ContentValues();
        receptionist.put(COL_USER_NAME, "Resepsionis 1");
        receptionist.put(COL_USER_EMAIL, "receptionist@arenakuin.com");
        receptionist.put(COL_USER_PASSWORD, "resep123");
        receptionist.put(COL_USER_PHONE, "081234567891");
        receptionist.put(COL_USER_ROLE, "receptionist");
        receptionist.put(COL_USER_CREATED_AT, getCurrentDateTime());
        db.insert(TABLE_USERS, null, receptionist);

        // ================= FUTSAL =================
        insertVenue(db, "Zona SM Futsal", "Futsal",
                "Jl. Sudimoro Utara No. 25, Mojolangu, Kec. Lowokwaru, Kota Malang",
                120000, "Buka 24 Jam, Parkir Luas, Musholla, Kantin", "futsal_zonasm", 4.5, "00.00", "24.00");

        insertVenue(db, "Wijaya Putra Futsal", "Futsal",
                "Jl. Tenaga Selatan No. 12, Purwantoro, Kec. Blimbing, Kota Malang",
                110000, "Lantai Interlock Bagus, Bersih, Parkir Mobil Luas", "futsal_wijaya", 4.4, "08.00", "22.00");

        insertVenue(db, "Unggul Sports Center Futsal", "Futsal",
                "Jl. Raya Karanglo No. 84, Karanglo, Banjararum, Kec. Singosari",
                150000, "Standar Internasional, Tribun Besar, Fasilitas Lengkap", "futsal_unggul", 4.8, "06.00", "22.00");

        insertVenue(db, "Nikolas Futsal 2", "Futsal",
                "Jl. Raya Mulyorejo, Mulyorejo, Kec. Sukun, Kota Malang",
                110000, "Strategis, Harga Terjangkau, Parkir Aman", "futsal_nikolas2", 4.5, "08.00", "24.00");

        insertVenue(db, "Lapangan Futsal UB Dieng", "Futsal",
                "Jl. Puncak Dieng Eksklusif, Kunci, Kalisongo, Kec. Dau",
                120000, "Sirkulasi udara sangat baik, Lantai Vinyl, Parkir Luas", "futsal_ubdieng", 4.9, "08.00", "22.00");

        // ================= BASKET =================
        insertVenue(db, "Mandala Sport Center Basket", "Basket",
                "Jl. Puncak Mandala No. 42-44, Pisang Candi, Kec. Sukun, Kota Malang",
                150000, "Indoor Besar, Tribun Penonton, Parkir Luas", "basket_mandala_sc", 4.4, "07.00", "22.00");

        insertVenue(db, "L8 Basketball Court", "Basket",
                "Jl. Saxophone No. 1, Tunggulwulung, Kec. Lowokwaru, Kota Malang",
                130000, "Semi-Indoor, Lantai Halus, Suasana Tenang", "basket_l8", 4.6, "08.00", "22.00");

        insertVenue(db, "The Black Lanners", "Basket",
                "Jl. Araya Mansion No. 8-9, Genitri, Tirtomoyo, Kec. Pakis",
                140000, "Premium Parquet, Scoreboard Digital, Exclusive", "basket_the_black_lanners", 4.7, "07.00", "22.00");

        insertVenue(db, "Hustle Basketball", "Basket",
                "Jl. Candi Panggung Indah No. 60, Mojolangu, Kec. Lowokwaru",
                120000, "Semi-Outdoor, Sejuk, Ada Cafe, Komunitas Ramai", "basket_hustle_basketball", 4.5, "07.00", "22.00");

        insertVenue(db, "Unggul SC Basketball", "Basket",
                "Jl. Raya Karanglo No. 84, Karanglo, Banjararum, Kec. Singosari",
                175000, "Standar FIBA, Full AC/Indoor, Tribun Premium", "basket_unggul_sc", 4.9, "06.00", "23.00");

        // ================= BADMINTON =================
        insertVenue(db, "GOR Uraha", "Badminton",
                "Jl. Simpang Sulfat Selatan, Pandanwangi, Kec. Blimbing",
                40000, "Karpet Hijau, Tribun Kecil, Harga Terjangkau", "badminton_gor_uraha", 4.4, "08.00", "23.00");

        insertVenue(db, "Platinum Araya", "Badminton",
                "Club House Araya, Jl. Raya Golf Utama, Tirtomoyo, Kec. Pakis",
                55000, "Hall Besar, Banyak Lapangan, Kantin, Parkir Luas", "badminton_platinum_araya", 4.7, "08.00", "23.00");

        insertVenue(db, "GOR Mulyakastara", "Badminton",
                "Jl. Raya Mulyorejo No. 100, Mulyorejo, Kec. Sukun, Kota Malang",
                45000, "Dinding Biru Khas, Pencahayaan Terang, Bersih", "badminton_gor_mulyakastara", 4.5, "07.00", "23.00");

        insertVenue(db, "Platinum Dieng", "Badminton",
                "Jl. Puncak Dieng Eksklusif, Kunci, Kalisongo, Kec. Dau",
                60000, "Premium Carpet, Bersih, Nyaman, Area Elite", "badminton_platinum_dieng", 4.8, "07.00", "23.00");

        insertVenue(db, "Unggul SC Badminton", "Badminton",
                "Jl. Raya Karanglo No. 84, Karanglo, Banjararum, Singosari",
                65000, "Professional Court, Luas, Event Ready", "badminton_unggul_sc", 4.8, "06.00", "22.00");

        // ================= GYM =================
        insertVenue(db, "Evolution Fitness", "Gym",
                "Jl. Candi Panggung No. 58, Mojolangu, Kec. Lowokwaru",
                40000, "Alat Smith Machine, Dumbbell Lengkap, Suasana Nyaman", "gym_evolution_fitness", 4.6, "07.00", "21.00");

        insertVenue(db, "Grit Fitness", "Gym",
                "Jl. Candi Telaga Wangi No. 26, Mojolangu, Kec. Lowokwaru",
                50000, "Desain Industrial Modern, Power Rack, Bersih", "gym_grit_fitness", 4.7, "06.00", "22.00");

        insertVenue(db, "DeGym Heritage", "Gym",
                "Jl. Jenderal Basuki Rahmat No. 11A-11B (Kayutangan), Kauman",
                75000, "Gedung Heritage, Premium, AC, Alat Import, Mewah", "gym_degym_heritage", 4.9, "06.00", "22.00");

        insertVenue(db, "Fitnessworks Black Lanners", "Gym",
                "Jl. Araya Mansion (Genitri), Tirtomoyo, Kec. Pakis",
                45000, "View Lapangan Basket, Modern, Alat Baru", "gym_fitnessworks_black_lanners", 4.7, "07.00", "21.00");

        insertVenue(db, "Smart Gym", "Gym",
                "Jl. Simpang Borobudur No. 1, Mojolangu, Kec. Lowokwaru",
                25000, "Alat Merah Lengkap, Ekonomis, Favorit Mahasiswa", "gym_smart", 4.4, "07.00", "21.00");

        // ================= PADEL =================
        insertVenue(db, "Padel Garden Araya", "Padel",
                "Jl. Mansion Hill No. 9, Genitri, Tirtomoyo (Araya), Kec. Pakis",
                250000, "Outdoor Premium, Karpet Pink/Biru, Coach Available", "padel_garden_araya", 4.8, "06.00", "22.00");

        insertVenue(db, "Mandala Sport Center Padel", "Padel",
                "Jl. Puncak Mandala No. 42, Pisang Candi, Kec. Sukun",
                200000, "Indoor Padel Pertama di Malang, Tribun", "padel_mandala_sc", 4.5, "07.00", "22.00");


        // ================= VOLI =================
        insertVenue(db, "GOR Ken Arok", "Voli",
                "Jl. Mayjen Sungkono, Buring, Kec. Kedungkandang",
                100000, "Indoor Besar, Tribun Stadion, Sering untuk Proliga", "voli_gor_ken_arok", 4.5, "07.00", "16.00");

        insertVenue(db, "Unggul SC Volleyball", "Voli",
                "Jl. Raya Karanglo No. 84, Singosari",
                120000, "Lantai Kayu/Parquet Premium, Fasilitas Top", "voli_unggul_sc", 4.7, "06.00", "23.00");

    }

    // --- Helper for seeding ---
    private void insertVenue(SQLiteDatabase db, String name, String type, String loc, double price, String fac, String img, double rate, String open, String close) {
        ContentValues values = new ContentValues();
        values.put(COL_VENUE_NAME, name);
        values.put(COL_VENUE_TYPE, type);
        values.put(COL_VENUE_LOCATION, loc);
        values.put(COL_VENUE_PRICE, price);
        values.put(COL_VENUE_FACILITIES, fac);
        values.put(COL_VENUE_IMAGE, img);
        values.put(COL_VENUE_RATING, rate);
        values.put(COL_VENUE_OPEN_TIME, open);
        values.put(COL_VENUE_CLOSE_TIME, close);
        values.put(COL_VENUE_IS_ACTIVE, 1);
        db.insert(TABLE_VENUES, null, values);
    }

    // --- CRUD OPERATIONS ---

    public long registerCustomer(String name, String email, String password, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_NAME, name);
        values.put(COL_USER_EMAIL, email);
        values.put(COL_USER_PASSWORD, password);
        values.put(COL_USER_PHONE, phone);
        values.put(COL_USER_ROLE, "customer");
        values.put(COL_USER_POINTS, 0);
        values.put(COL_USER_MEMBERSHIP, "Regular");
        values.put(COL_USER_CREATED_AT, getCurrentDateTime());
        return db.insert(TABLE_USERS, null, values);
    }

    public Cursor loginUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USERS, null, COL_USER_EMAIL + "=? AND " + COL_USER_PASSWORD + "=?", new String[]{email, password}, null, null, null);
    }

    public Cursor getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USERS, null, COL_USER_ID + "=?", new String[]{String.valueOf(userId)}, null, null, null);
    }

    public Cursor getAllActiveVenues() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_VENUES, null, COL_VENUE_IS_ACTIVE + "=1", null, null, null, COL_VENUE_RATING + " DESC");
    }

    public Cursor getVenuesByType(String type) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_VENUES, null, COL_VENUE_TYPE + "=? AND " + COL_VENUE_IS_ACTIVE + "=1", new String[]{type}, null, null, COL_VENUE_RATING + " DESC");
    }

    public Cursor getVenueById(int venueId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_VENUES, null, COL_VENUE_ID + "=?", new String[]{String.valueOf(venueId)}, null, null, null);
    }

    // --- FITUR ADMIN: MANAJEMEN VENUE ---
    public Cursor getAllVenues() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_VENUES + " ORDER BY " + COL_VENUE_NAME + " ASC", null);
    }

    public boolean toggleVenueStatus(int venueId, boolean isActive) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_VENUE_IS_ACTIVE, isActive ? 1 : 0);
        int rows = db.update(TABLE_VENUES, values, COL_VENUE_ID + " = ?", new String[]{String.valueOf(venueId)});
        return rows > 0;
    }

    // --- FITUR BOOKING ---
    public long createBooking(int userId, int venueId, String date, String startTime, String endTime, double totalPrice, String paymentMethod, String bookingType, int createdBy, String notes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_BOOKING_USER_ID, userId);
        values.put(COL_BOOKING_VENUE_ID, venueId);
        values.put(COL_BOOKING_DATE, date);
        values.put(COL_BOOKING_START_TIME, startTime);
        values.put(COL_BOOKING_END_TIME, endTime);
        values.put(COL_BOOKING_TOTAL_PRICE, totalPrice);
        values.put(COL_BOOKING_STATUS, "Pending");
        values.put(COL_BOOKING_PAYMENT_STATUS, "Unpaid");
        values.put(COL_BOOKING_PAYMENT_METHOD, paymentMethod);
        values.put(COL_BOOKING_TYPE, bookingType);
        values.put(COL_BOOKING_NOTES, notes);
        values.put(COL_BOOKING_CREATED_AT, getCurrentDateTime());
        values.put(COL_BOOKING_CREATED_BY, createdBy);
        return db.insert(TABLE_BOOKINGS, null, values);
    }

    public Cursor getUserBookings(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT b.*, v." + COL_VENUE_NAME + ", v." + COL_VENUE_TYPE + " FROM " + TABLE_BOOKINGS + " b INNER JOIN " + TABLE_VENUES + " v ON b." + COL_BOOKING_VENUE_ID + " = v." + COL_VENUE_ID + " WHERE b." + COL_BOOKING_USER_ID + " = ? ORDER BY b." + COL_BOOKING_CREATED_AT + " DESC";
        return db.rawQuery(query, new String[]{String.valueOf(userId)});
    }

    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    public Cursor checkAvailability(int venueId, String date, String startTime, String endTime) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COL_BOOKING_VENUE_ID + "=? AND " +
                COL_BOOKING_DATE + "=? AND " +
                COL_BOOKING_STATUS + " NOT IN ('Cancelled', 'Rejected') AND " +
                "(" + COL_BOOKING_START_TIME + " < ? AND " + COL_BOOKING_END_TIME + " > ?)";
        String[] selectionArgs = new String[]{String.valueOf(venueId), date, endTime, startTime};
        return db.query(TABLE_BOOKINGS, null, selection, selectionArgs, null, null, null);
    }

    public boolean isFavorite(int userId, int venueId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_FAVORITES + " WHERE " + COL_FAV_USER_ID + " = ? AND " + COL_FAV_VENUE_ID + " = ?",
                new String[]{String.valueOf(userId), String.valueOf(venueId)});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public void addToFavorites(int userId, int venueId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_FAV_USER_ID, userId);
        values.put(COL_FAV_VENUE_ID, venueId);
        db.insert(TABLE_FAVORITES, null, values);
    }

    public void removeFromFavorites(int userId, int venueId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FAVORITES, COL_FAV_USER_ID + " = ? AND " + COL_FAV_VENUE_ID + " = ?",
                new String[]{String.valueOf(userId), String.valueOf(venueId)});
    }

    public Cursor getVenueReviews(int venueId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT r." + COL_REVIEW_ID + ", r." + COL_REVIEW_USER_ID + ", r." + COL_REVIEW_RATING + ", r." + COL_REVIEW_COMMENT + ", r." + COL_REVIEW_DATE + ", u." + COL_USER_NAME + " " +
                "FROM " + TABLE_REVIEWS + " r " +
                "JOIN " + TABLE_USERS + " u ON r." + COL_REVIEW_USER_ID + " = u." + COL_USER_ID + " " +
                "WHERE r." + COL_REVIEW_VENUE_ID + " = ? " +
                "ORDER BY r." + COL_REVIEW_DATE + " DESC";
        return db.rawQuery(query, new String[]{String.valueOf(venueId)});
    }

    // --- FITUR ADMIN: MANAJEMEN BOOKING ---
    public Cursor getAllBookings() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT b.*, u." + COL_USER_NAME + " AS customer_name, " +
                "v." + COL_VENUE_NAME + ", v." + COL_VENUE_TYPE + " " +
                "FROM " + TABLE_BOOKINGS + " b " +
                "JOIN " + TABLE_USERS + " u ON b." + COL_BOOKING_USER_ID + " = u." + COL_USER_ID + " " +
                "JOIN " + TABLE_VENUES + " v ON b." + COL_BOOKING_VENUE_ID + " = v." + COL_VENUE_ID + " " +
                "ORDER BY b." + COL_BOOKING_DATE + " DESC, b." + COL_BOOKING_START_TIME + " DESC";
        return db.rawQuery(query, null);
    }

    public Cursor getBookingsByStatus(String status) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT b.*, u." + COL_USER_NAME + " AS customer_name, " +
                "v." + COL_VENUE_NAME + ", v." + COL_VENUE_TYPE + " " +
                "FROM " + TABLE_BOOKINGS + " b " +
                "JOIN " + TABLE_USERS + " u ON b." + COL_BOOKING_USER_ID + " = u." + COL_USER_ID + " " +
                "JOIN " + TABLE_VENUES + " v ON b." + COL_BOOKING_VENUE_ID + " = v." + COL_VENUE_ID + " " +
                "WHERE b." + COL_BOOKING_STATUS + " = ? " +
                "ORDER BY b." + COL_BOOKING_DATE + " DESC";
        return db.rawQuery(query, new String[]{status});
    }

    public boolean updateBookingStatus(int bookingId, String newStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_BOOKING_STATUS, newStatus);
        if (newStatus.equals("Completed") || newStatus.equals("Confirmed")) {
            values.put(COL_BOOKING_PAYMENT_STATUS, "Paid");
        }
        int rowsAffected = db.update(TABLE_BOOKINGS, values, COL_BOOKING_ID + " = ?", new String[]{String.valueOf(bookingId)});
        return rowsAffected > 0;
    }

    // ==========================================
    //       FITUR ADMIN: STATISTIK DASHBOARD
    // ==========================================

    public int getTotalBookingsCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_BOOKINGS, null);
        int count = 0;
        if (cursor.moveToFirst()) count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    public int getTodayBookingsCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_BOOKINGS + " WHERE " + COL_BOOKING_DATE + " = ?", new String[]{today});
        int count = 0;
        if (cursor.moveToFirst()) count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    public double getTodayRevenue() {
        SQLiteDatabase db = this.getReadableDatabase();
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Cursor cursor = db.rawQuery("SELECT SUM(" + COL_BOOKING_TOTAL_PRICE + ") FROM " + TABLE_BOOKINGS +
                        " WHERE " + COL_BOOKING_DATE + " = ? AND " + COL_BOOKING_STATUS + " != 'Cancelled'",
                new String[]{today});
        double revenue = 0;
        if (cursor.moveToFirst()) revenue = cursor.getDouble(0);
        cursor.close();
        return revenue;
    }

    public int getPendingPaymentsCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_BOOKINGS +
                " WHERE " + COL_BOOKING_PAYMENT_STATUS + " = 'Unpaid' OR " + COL_BOOKING_STATUS + " = 'Pending'", null);
        int count = 0;
        if (cursor.moveToFirst()) count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    // --- FITUR ADMIN: PAYMENTS ---
    public Cursor getPendingPayments() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT b.*, u." + COL_USER_NAME + " AS customer_name, " +
                "v." + COL_VENUE_NAME + " " +
                "FROM " + TABLE_BOOKINGS + " b " +
                "JOIN " + TABLE_USERS + " u ON b." + COL_BOOKING_USER_ID + " = u." + COL_USER_ID + " " +
                "JOIN " + TABLE_VENUES + " v ON b." + COL_BOOKING_VENUE_ID + " = v." + COL_VENUE_ID + " " +
                "WHERE b." + COL_BOOKING_STATUS + " = 'Pending' " +
                "ORDER BY b." + COL_BOOKING_DATE + " ASC";
        return db.rawQuery(query, null);
    }

    public boolean updatePaymentStatus(int bookingId, String paymentStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_BOOKING_PAYMENT_STATUS, paymentStatus);
        if ("Paid".equalsIgnoreCase(paymentStatus)) {
            values.put(COL_BOOKING_STATUS, "Confirmed");
        }
        int rows = db.update(TABLE_BOOKINGS, values, COL_BOOKING_ID + " = ?", new String[]{String.valueOf(bookingId)});
        return rows > 0;
    }

    // --- FITUR FAVORITE (USER) ---
    public Cursor getUserFavorites(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        // Query: Ambil semua kolom Venue (v.*) dimana ID-nya ada di tabel Favorites milik user ini
        String query = "SELECT v.* " +
                "FROM " + TABLE_VENUES + " v " +
                "JOIN " + TABLE_FAVORITES + " f ON v." + COL_VENUE_ID + " = f." + COL_FAV_VENUE_ID + " " +
                "WHERE f." + COL_FAV_USER_ID + " = ?";
        return db.rawQuery(query, new String[]{String.valueOf(userId)});
    }
}