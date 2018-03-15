package bulcina.bcpl;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;


public class BulcinaDatabaseHelper extends SQLiteOpenHelper{
    private static BulcinaDatabaseHelper mInstance = null;

    private static final String DATABASE_NAME = "bulcina.db";
    private static final int DATABASE_VERSION = 8;

    static final String TABLE_BULCINA = "bulcina";
    public static final String BULCINA_COLUMN_ID = "bulcina_id";
    public static final String BULCINA_COLUMN_NOSAUKUMS = "nosaukums";
    public static final String BULCINA_COLUMN_PASIZMAKSA = "pasizmaksa";
    public static final String BULCINA_COLUMN_REALIZACIJA = "realizacija";
    public static final String BULCINA_COLUMN_NEREALIZETAIS = "nerealizetais";
    public static final String BULCINA_COLUMN_ATTELS = "attels";
    public static final String BULCINA_COLUMN_PROGNOZE_DARBADIENA = "prognoze_darbadiena";
    public static final String BULCINA_COLUMN_PROGNOZE_BRIVDIENA = "prognoze_brivdiena";

    private static final String TABLE_BULCINAS_CREATE = "create table "+TABLE_BULCINA+"("+
            BULCINA_COLUMN_ID + " integer primary key autoincrement, "+
            BULCINA_COLUMN_NOSAUKUMS + " text not null, "+
            BULCINA_COLUMN_PASIZMAKSA  + " real not null, "+
            BULCINA_COLUMN_REALIZACIJA  + " real not null, "+
            BULCINA_COLUMN_NEREALIZETAIS + " real not null, " +
            BULCINA_COLUMN_ATTELS + " blob, " +
            BULCINA_COLUMN_PROGNOZE_DARBADIENA + " real, " +
            BULCINA_COLUMN_PROGNOZE_BRIVDIENA + " real" + ");";

    static final String TABLE_PIEPRASIJUMS = "pieprasijums";
    public static final String PIEPRASIJUMS_COLUMN_ID = "pieprasijums_id";
    public static final String PIEPRASIJUMS_COLUMN_DATUMS = "datums";
    public static final String PIEPRASIJUMS_COLUMN_PIEPRASIJUMS = "pieprasijums";
    public static final String PIEPRASIJUMS_COLUMN_PROGNOZE = "prognoze";
    public static final String PIEPRASIJUMS_COLUMN_DARBADIENA = "darbadiena";

    private static final String TABLE_PIEPRASIJUMS_CREATE = "create table "+TABLE_PIEPRASIJUMS+"("+
            PIEPRASIJUMS_COLUMN_ID + " integer primary key autoincrement, "+
            PIEPRASIJUMS_COLUMN_DATUMS + " timestamp default current_timestamp, "+
            PIEPRASIJUMS_COLUMN_PIEPRASIJUMS + " integer not null, "+
            PIEPRASIJUMS_COLUMN_PROGNOZE  + " real, "+
            PIEPRASIJUMS_COLUMN_DARBADIENA + " integer, " +
            BULCINA_COLUMN_ID + " integer not null" +");";

    public static BulcinaDatabaseHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new BulcinaDatabaseHelper(context.getApplicationContext());
        }
        return mInstance;
    }

    private BulcinaDatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(TABLE_BULCINAS_CREATE);
        db.execSQL(TABLE_PIEPRASIJUMS_CREATE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BULCINA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PIEPRASIJUMS);
        onCreate(db);
    }

    public boolean addBulcina(String nosaukums, double pasizmaksa, double realizacija, double nerealizetais, byte[] attels){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues bulcValues = new ContentValues();

        try {
            bulcValues.put(BULCINA_COLUMN_NOSAUKUMS, nosaukums);
            bulcValues.put(BULCINA_COLUMN_PASIZMAKSA, pasizmaksa);
            bulcValues.put(BULCINA_COLUMN_REALIZACIJA, realizacija);
            bulcValues.put(BULCINA_COLUMN_NEREALIZETAIS, nerealizetais);
            bulcValues.put(BULCINA_COLUMN_ATTELS, attels);
            bulcValues.put(BULCINA_COLUMN_PROGNOZE_DARBADIENA, 0);
            bulcValues.put(BULCINA_COLUMN_PROGNOZE_BRIVDIENA, 0);

            db.insert(TABLE_BULCINA, null, bulcValues);
            return true;
        }
        catch (Exception e){
            Log.e("BCPL","Kluda bulcinas pievienosana.",e);
            return false;
        }
    }

    public void addPieprasijums(int pieprasijums, int darbadiena, int bulcina_id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues pieprValues = new ContentValues();

        pieprValues.put(PIEPRASIJUMS_COLUMN_PIEPRASIJUMS, pieprasijums);
        pieprValues.put(PIEPRASIJUMS_COLUMN_PROGNOZE, getPasreizejoPrognozi(bulcina_id,darbadiena));
        pieprValues.put(PIEPRASIJUMS_COLUMN_DARBADIENA, darbadiena);
        pieprValues.put(BULCINA_COLUMN_ID, bulcina_id);

        db.insert(TABLE_PIEPRASIJUMS, null, pieprValues);

        updatePasreizejoPrognozi(bulcina_id, darbadiena);
    }

    public Cursor getAllBulcinas(){
        SQLiteDatabase db = this.getReadableDatabase();
        String strVaic = "select " + BULCINA_COLUMN_ID +" as _id, " +
                BULCINA_COLUMN_NOSAUKUMS + ", " +
                //BULCINA_COLUMN_PASIZMAKSA + ", " +
                //BULCINA_COLUMN_REALIZACIJA + ", " +
                //BULCINA_COLUMN_NEREALIZETAIS + ", " +
                //BULCINA_COLUMN_ATTELS + ", " +
                BULCINA_COLUMN_PROGNOZE_DARBADIENA + ", " +
                BULCINA_COLUMN_PROGNOZE_BRIVDIENA +
                " from " + TABLE_BULCINA;

        return db.rawQuery(strVaic, null);
    }

    public Cursor getBulcina(int bulcina_id){
        SQLiteDatabase db = this.getReadableDatabase();
        String strVaic = "select " + BULCINA_COLUMN_ID +", " +
                BULCINA_COLUMN_NOSAUKUMS + ", " +
                BULCINA_COLUMN_PASIZMAKSA + ", " +
                BULCINA_COLUMN_REALIZACIJA + ", " +
                BULCINA_COLUMN_NEREALIZETAIS + ", " +
                BULCINA_COLUMN_ATTELS + ", " +
                BULCINA_COLUMN_PROGNOZE_DARBADIENA + ", " +
                BULCINA_COLUMN_PROGNOZE_BRIVDIENA +
                " from " + TABLE_BULCINA +
                " where " + BULCINA_COLUMN_ID + "=" + bulcina_id;

        return db.rawQuery(strVaic, null);
    }

    public Cursor getBulcinaAttels(int bulcina_id){
        SQLiteDatabase db = this.getReadableDatabase();
        String strVaic = "select " + BULCINA_COLUMN_ATTELS +
                " from " + TABLE_BULCINA +
                " where " + BULCINA_COLUMN_ID + "=" + bulcina_id;

        return db.rawQuery(strVaic, null);
    }

    public Cursor getBulcinasPieprasijumu(int bulcina_id){
        SQLiteDatabase db = this.getReadableDatabase();
        String strVaic = "select " + PIEPRASIJUMS_COLUMN_ID +" as _id, " +
                PIEPRASIJUMS_COLUMN_DATUMS + ", " +
                PIEPRASIJUMS_COLUMN_PIEPRASIJUMS + ", " +
                PIEPRASIJUMS_COLUMN_PROGNOZE + ", " +
                PIEPRASIJUMS_COLUMN_DARBADIENA + ", " +
                BULCINA_COLUMN_ID +
                " from " + TABLE_PIEPRASIJUMS +
                " where " + BULCINA_COLUMN_ID + "=" + bulcina_id;

        return db.rawQuery(strVaic, null);
    }

    public Cursor getBulcinasPieprasijumuSezona(int bulcina_id, int darbadiena){
        SQLiteDatabase db = this.getReadableDatabase();
        String strVaic = "select *" +
                " from " + TABLE_PIEPRASIJUMS +
                " where " + BULCINA_COLUMN_ID + "=" + bulcina_id +
                " and " + PIEPRASIJUMS_COLUMN_DARBADIENA + "=" + darbadiena +
                " order by " + PIEPRASIJUMS_COLUMN_ID + " desc limit 30";

        return db.rawQuery(strVaic, null);
    }

    public double getPasreizejoPrognozi(int bulcina_id, int darbadiena){
        double prognoze;
        Cursor c =  getBulcina(bulcina_id);
        String diena;

        c.moveToFirst();
        if (darbadiena==1){
            diena = BULCINA_COLUMN_PROGNOZE_DARBADIENA;
        }
        else {
            diena = BULCINA_COLUMN_PROGNOZE_BRIVDIENA;
        }

        prognoze = c.getDouble(c.getColumnIndexOrThrow(diena));

        return prognoze;
    }

    public boolean updateBulcina(int bulcina_id, String nosaukums, double pasizmaksa, double realizacija, double nerealizetais, byte[] attels){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues bulcValues = new ContentValues();
        String where;
        where = BULCINA_COLUMN_ID + " = ?";

        try {
            bulcValues.put(BULCINA_COLUMN_NOSAUKUMS, nosaukums);
            bulcValues.put(BULCINA_COLUMN_PASIZMAKSA, pasizmaksa);
            bulcValues.put(BULCINA_COLUMN_REALIZACIJA, realizacija);
            bulcValues.put(BULCINA_COLUMN_NEREALIZETAIS, nerealizetais);
            bulcValues.put(BULCINA_COLUMN_ATTELS, attels);

            db.update(TABLE_BULCINA, bulcValues, where, new String[] { Integer.toString(bulcina_id) });
        }
        catch (Exception e){
            Log.e("BCPL","Kluda bulcinas redigesana",e);
            return false;
        }
        return true;
    }

    public void updatePasreizejoPrognozi(int bulcina_id, int darbadiena){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues bulcValues = new ContentValues();
        String diena;
        String where;

        if (darbadiena==1){
            diena = BULCINA_COLUMN_PROGNOZE_DARBADIENA;
        }
        else {
            diena = BULCINA_COLUMN_PROGNOZE_BRIVDIENA;
        }

        where = BULCINA_COLUMN_ID + " = ?";

        try {
            bulcValues.put(diena, prognozetPieprasijumu(bulcina_id, darbadiena));
        }
        catch (ArithmeticException ae){
            bulcValues.put(diena, 0);
        }
        db.update(TABLE_BULCINA, bulcValues, where, new String[] { Integer.toString(bulcina_id) });
    }

    public void deleteBulcina (int bulcina_id){
        SQLiteDatabase db = this.getWritableDatabase();
        String where;

        where = BULCINA_COLUMN_ID + " = ?";

        db.delete(TABLE_BULCINA, where, new String[] { Integer.toString(bulcina_id) });
        db.delete(TABLE_PIEPRASIJUMS, where, new String[] { Integer.toString(bulcina_id) });
    }

    public void deletePieprasijums (int pieprasijums_id){
        SQLiteDatabase db = this.getWritableDatabase();
        String where;

        where = PIEPRASIJUMS_COLUMN_ID + " = ?";
        db.delete(TABLE_PIEPRASIJUMS, where, new String[] { Integer.toString(pieprasijums_id) });
    }

    public void deleteAllPieprasijums(int bulcina_id){
        SQLiteDatabase db = this.getWritableDatabase();
        String where;

        where = BULCINA_COLUMN_ID + " = ?";

        db.delete(TABLE_PIEPRASIJUMS, where, new String[] { Integer.toString(bulcina_id) });
    }

    public Cursor mekletPecDatuma(int bulcina_id, String datums){
        SQLiteDatabase db = this.getReadableDatabase();
        String strVaic = "select " + PIEPRASIJUMS_COLUMN_ID +" as _id, " +
                PIEPRASIJUMS_COLUMN_DATUMS + ", " +
                PIEPRASIJUMS_COLUMN_PIEPRASIJUMS + ", " +
                PIEPRASIJUMS_COLUMN_PROGNOZE + ", " +
                PIEPRASIJUMS_COLUMN_DARBADIENA + ", " +
                BULCINA_COLUMN_ID +
                " from " + TABLE_PIEPRASIJUMS +
                " where " + BULCINA_COLUMN_ID + "=" + bulcina_id +
                " and " + PIEPRASIJUMS_COLUMN_DATUMS + " like '" + datums + "%'";

        return db.rawQuery(strVaic, null);
    }

    //parskata metodes

    public double getBulcinasPelnu(int bulcina_id){
        double pelna;
        double pardotaisApjoms = 0;
        double cena;
        double pieprasijums;
        double prognoze;

        Cursor cb = getBulcina(bulcina_id);
        cb.moveToFirst();
        cena= cb.getDouble(cb.getColumnIndexOrThrow(BULCINA_COLUMN_REALIZACIJA));


        Cursor cp = getBulcinasPieprasijumu(bulcina_id);

        if(cp.getCount()==0){
            return 0;
        }

        cp.moveToFirst();

        do{
            pieprasijums = cp.getDouble(cp.getColumnIndexOrThrow(PIEPRASIJUMS_COLUMN_PIEPRASIJUMS));
            prognoze = cp.getDouble(cp.getColumnIndexOrThrow(PIEPRASIJUMS_COLUMN_PROGNOZE));

            if(pieprasijums >= prognoze){
                pardotaisApjoms = pardotaisApjoms + prognoze;
            }
            else{
                pardotaisApjoms = pardotaisApjoms + pieprasijums;
            }

        }while(cp.moveToNext());


        pelna = cena * pardotaisApjoms;

        cp.close();
        cb.close();

        return pelna;

    }
    public double getBulcinasApjoms(int bulcina_id){
        double pardotaisApjoms = 0;
        double pieprasijums;
        double prognoze;

        Cursor cp = getBulcinasPieprasijumu(bulcina_id);

        if(cp.getCount()==0){
            return 0;
        }

        cp.moveToFirst();

        do{
            pieprasijums = cp.getDouble(cp.getColumnIndexOrThrow(PIEPRASIJUMS_COLUMN_PIEPRASIJUMS));
            prognoze = cp.getDouble(cp.getColumnIndexOrThrow(PIEPRASIJUMS_COLUMN_PROGNOZE));

            if(pieprasijums >= prognoze){
                pardotaisApjoms = pardotaisApjoms + prognoze;
            }
            else{
                pardotaisApjoms = pardotaisApjoms + pieprasijums;
            }

        }while(cp.moveToNext());


        return pardotaisApjoms;
    }

    public double getPrognozesPrecizitati(int bulcina_id){
        double [] precizitate;
        double prognoze;
        double pieprasijums;
        double videjaPrecizitate = 0;
        int i = 0;

        Cursor cp = getBulcinasPieprasijumu(bulcina_id);

        if(cp.getCount()==0){
            return 0;
        }

        cp.moveToFirst();

        precizitate = new double[cp.getCount()];

        Log.d("BCPL","Vidējais=" + cp.getCount() );
        do{
            prognoze = cp.getDouble(cp.getColumnIndexOrThrow(PIEPRASIJUMS_COLUMN_PROGNOZE));
            Log.d("BCPL","Prognoze = " + prognoze) ;
            pieprasijums = cp.getDouble(cp.getColumnIndexOrThrow(PIEPRASIJUMS_COLUMN_PIEPRASIJUMS));
            Log.d("BCPL","Peiprasij = " + pieprasijums );
            if(pieprasijums > prognoze){
                precizitate[i] = Math.round(100 - (Math.abs(pieprasijums - prognoze)/pieprasijums)*100);
            }
            else{
                precizitate[i] = Math.round(100 - (Math.abs(prognoze - pieprasijums)/prognoze)*100);
            }
            Log.d("BCPL","Precizit = " + precizitate[i] );
            i++;

        }while(cp.moveToNext());

        for(i = 0; i<cp.getCount(); i++){
            videjaPrecizitate +=precizitate[i];
        }
        videjaPrecizitate = videjaPrecizitate/cp.getCount();
        return videjaPrecizitate;
    }

    // bulcinas datu bazes eksportesana uz arejo atminu
    public boolean exportDB (Context context){
        try {
            String currentDBPath = context.getDatabasePath(DATABASE_NAME).getPath();
            String exportFileName = Environment.getExternalStorageDirectory() + "/" + DATABASE_NAME;
            File dbFile = new File(currentDBPath);
            FileInputStream fis = new FileInputStream(dbFile);
            OutputStream output = new FileOutputStream(exportFileName);

            byte[] buffer = new byte[1024];
            int length;
            while((length = fis.read(buffer)) > 0){
                output.write(buffer, 0, length);
            }

            output.flush();
            output.close();
            fis.close();
        }
        catch (Exception e){
            Log.e("BCPL","Kluda DB eksportesana",e);
            return false;
        }
        return true;
    }

    // bulcinas datu bazes importesana no arejas atminas
    public boolean importDB (Context context){
        try {
            File bulcinaDB = new File(Environment.getExternalStorageDirectory() + "/" + DATABASE_NAME );
            if(!bulcinaDB.exists()){
                return false;
            }

            String currentDBPath = context.getDatabasePath(DATABASE_NAME).getPath();
            String importFileName = Environment.getExternalStorageDirectory() + "/" + DATABASE_NAME;
            File dbFile = new File(importFileName);
            FileInputStream fis = new FileInputStream(dbFile);
            OutputStream output = new FileOutputStream(currentDBPath);

            byte[] buffer = new byte[1024];
            int length;
            while((length = fis.read(buffer)) > 0){
                output.write(buffer, 0, length);
            }

            output.flush();
            output.close();
            fis.close();
        }
        catch (Exception e){
            Log.e("BCPL","Kluda DB importesana",e);
            return false;
        }
        return true;
    }

    //bulcinu datu bazes backup veidosana
    public boolean backupDB (Context context){
        try {
            String backupFileName = Environment.getExternalStorageDirectory() + "/BCPL_backup/" + DATABASE_NAME;
            File backupFile = new File(backupFileName);
            if (backupFile.exists()){
                long msDiff = Calendar.getInstance().getTimeInMillis() - backupFile.lastModified();
                long daysDiff = TimeUnit.MILLISECONDS.toDays(msDiff);
                //testesana
                //Log.d("datumu atskiriba=", Long.toString(daysDiff));
                if (daysDiff>=7){
                    String currentDBPath = context.getDatabasePath(DATABASE_NAME).getPath();
                    File dbFile = new File(currentDBPath);
                    FileInputStream fis = new FileInputStream(dbFile);
                    OutputStream output = new FileOutputStream(backupFileName);

                    byte[] buffer = new byte[1024];
                    int length;
                    while((length = fis.read(buffer)) > 0){
                        output.write(buffer, 0, length);
                    }

                    output.flush();
                    output.close();
                    fis.close();
                    return true;
                }
            }
            else {
                String backupFileDir = Environment.getExternalStorageDirectory() + "/BCPL_backup/";
                File backupDir = new File(backupFileDir);
                backupDir.mkdir();
                String currentDBPath = context.getDatabasePath(DATABASE_NAME).getPath();
                File dbFile = new File(currentDBPath);
                FileInputStream fis = new FileInputStream(dbFile);
                OutputStream output = new FileOutputStream(backupFileName);

                byte[] buffer = new byte[1024];
                int length;
                while((length = fis.read(buffer)) > 0){
                    output.write(buffer, 0, length);
                }

                output.flush();
                output.close();
                fis.close();
                return true;
            }
        }
        catch (Exception e){
            Log.e("BCPL","Kluda DB backup veidosana",e);
            return false;
        }
        return false;
    }

    public double prognozetPieprasijumu(int bulcina_id, int darbadiena){
        double prognoze;
        double standartnovirze;
        double summa;
        double videjais;
        double pasizmaksa;
        double realizacija;
        double nerealizetais;
        double ctr;
        double cl;
        double pa;
        double samazinat;
        double palielinat;
        int skaits;

        Cursor cp = getBulcinasPieprasijumuSezona(bulcina_id, darbadiena);
        Cursor cb = getBulcina(bulcina_id);

        cb.moveToFirst();

        pasizmaksa = cb.getDouble(cb.getColumnIndexOrThrow(BULCINA_COLUMN_PASIZMAKSA));
        realizacija = cb.getDouble(cb.getColumnIndexOrThrow(BULCINA_COLUMN_REALIZACIJA));
        nerealizetais = cb.getDouble(cb.getColumnIndexOrThrow(BULCINA_COLUMN_NEREALIZETAIS));

        ctr = realizacija - pasizmaksa;
        cl = pasizmaksa - nerealizetais;

        pa = ctr/(cl+ctr);

        //Vai nu atgriez nulli, vai ari iziet no nulles cikla, ja nav datu, tacu ir izdevigi
        // uznemties neveiksmes risku. Lielums 100 (simts) norada ka jarada nevis
        // vienkarsi simts bulcinjas bet simts VIENIBAS - 1)kastes, 2)vagoni, 3)ari bulcinjas
        // atkariba kados apjomos reali razo.
        if (cp.getCount()==0){
            if (pa>=0.70){
                return 100;
            }
            return 0;
        }

        cp.moveToFirst();

        summa = cp.getDouble(cp.getColumnIndexOrThrow(PIEPRASIJUMS_COLUMN_PIEPRASIJUMS));
        while (cp.moveToNext()){
            summa = summa + cp.getDouble(cp.getColumnIndexOrThrow(PIEPRASIJUMS_COLUMN_PIEPRASIJUMS));
        }

        skaits = cp.getCount();
        videjais = summa/skaits;

        //testešana
        //Log.d("BCPL","Videjais=" + videjais );

        cp.moveToFirst();

        standartnovirze = Math.pow(cp.getInt(cp.getColumnIndexOrThrow(PIEPRASIJUMS_COLUMN_PIEPRASIJUMS)) - videjais, 2);

        while (cp.moveToNext()){
            standartnovirze = standartnovirze + Math.pow(cp.getDouble(cp.getColumnIndexOrThrow(PIEPRASIJUMS_COLUMN_PIEPRASIJUMS)) - videjais, 2);
        }

        // Jo nevar dalit ar nulli, kad skaits skaits == 1
        if (skaits==1){
            standartnovirze = 0;
        }
        else {
            standartnovirze = Math.sqrt(standartnovirze/(skaits-1));
        }

        //testešana
        //Log.d("BCPL","Standartnovirze=" + standartnovirze );

        //testešana
        //Log.d("BCPL","Z=" + NormalCDFInverse(pa) );

        // Rekina ne tikai prognozi, bet nem vera ari ekonomisko izdevigumu
        if (pa >= 0.9) {
            palielinat = 5*pa;

            prognoze = palielinat*videjais+standartnovirze*NormalCDFInverse(pa);
        }
        else if (pa <= 0.1){
            double novirze = standartnovirze*NormalCDFInverse(pa);
            if (novirze>=0){
                samazinat = 5*pa;
                prognoze = samazinat*videjais+standartnovirze*NormalCDFInverse(pa);
            }
            else {
                samazinat = 0.9;
                prognoze = samazinat*(videjais+standartnovirze*NormalCDFInverse(pa));
            }

        }

        else {
            prognoze = videjais+standartnovirze*NormalCDFInverse(pa);
        }

        //testešana
        //Log.d("BCPL","Prognoze=" + prognoze );

        cp.close();
        cb.close();

        return prognoze;
    }

    public double RationalApproximation(double t)
    {
        // Abramowitz and Stegun formula 26.2.23.
        // The absolute value of the error should be less than 4.5 e-4.
        double c[] = {2.515517, 0.802853, 0.010328};
        double d[] = {1.432788, 0.189269, 0.001308};
        return t - ((c[2]*t + c[1])*t + c[0]) /
                (((d[2]*t + d[1])*t + d[0])*t + 1.0);
    }

    public double NormalCDFInverse(double p){
        if (p < 0.5)
        {
            // F^-1(p) = - G^-1(p)
            return -RationalApproximation( Math.sqrt(-2.0*Math.log(p)));
        }
        else
        {
            // F^-1(p) = G^-1(1-p)
            return RationalApproximation( Math.sqrt(-2.0*Math.log(1-p)));
        }
    }

}
