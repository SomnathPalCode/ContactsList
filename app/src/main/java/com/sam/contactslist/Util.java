package com.sam.contactslist;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;

import java.io.InputStream;
import java.util.ArrayList;

public class Util {

//    public static ArrayList<Contact> getContacts(Context ctx) {
//        ArrayList<Contact> list = new ArrayList<>();
//        try{
//            ContentResolver contentResolver = ctx.getContentResolver();
//            Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
//            if (Objects.requireNonNull(cursor).getCount() > 0) {
//                while (cursor.moveToNext()) {
//                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
//                    if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
//                        Cursor cursorInfo = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
//                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
//                        InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(ctx.getContentResolver(),
//                                ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(id)));
//
//                        Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(id));
//                        Uri pURI = Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
//
//                        Bitmap photo = null;
//                        if (inputStream != null) {
//                            photo = BitmapFactory.decodeStream(inputStream);
//                        }
//                        while (Objects.requireNonNull(cursorInfo).moveToNext()) {
//                            Contact info = new Contact();
//                            //info._id = id;
//                            info.name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//                            info.mobile = cursorInfo.getString(cursorInfo.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                            info.profile = photo;
//                            //info.photoURI= pURI;
//                            list.add(info);
//                        }
//                        cursorInfo.close();
//
//                        Cursor cur1 = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
//                                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
//                                new String[]{id}, null);
//                        while (Objects.requireNonNull(cur1).moveToNext()) {
//                            //to get the contact names
//                            String name=cur1.getString(cur1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
//                            String email = cur1.getString(cur1.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
//                            System.out.println("SAM: email: "+email);
//                            if(email!=null){
//                                list.add(name);
//                            }
//                        }
//                        cur1.close();
//                    }
//                }
//                cursor.close();
//            }
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//        return list;
//    }


    public static ArrayList<Contact> getContacts(Context ctx){
        // https://gist.github.com/srayhunter/47ab2816b01f0b00b79150150feb2eb2
        try {
            final String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY;
            final String FILTER = DISPLAY_NAME + " NOT LIKE '%@%'";
            final String ORDER = String.format("%1$s COLLATE NOCASE", DISPLAY_NAME);
            final String[] PROJECTION = {
                    ContactsContract.Contacts._ID,
                    DISPLAY_NAME,
                    ContactsContract.Contacts.HAS_PHONE_NUMBER
            };
            ArrayList<Contact> contacts = new ArrayList<>();

            ContentResolver cr = ctx.getContentResolver();
            Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, PROJECTION, FILTER, null, ORDER);
            if (cursor != null && cursor.moveToFirst()) {

                do {
                    // get the user's id, name
                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                    Integer hasPhone = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                    // get the user's email address
                    String email = null;
                    Cursor ce = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id}, null);
                    if (ce != null && ce.moveToFirst()) {
                        email = ce.getString(ce.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        ce.close();
                    }

                    // get the user's phone number
                    String phone = null;
                    if (hasPhone > 0) {
                        Cursor cp = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                        if (cp != null && cp.moveToFirst()) {
                            phone = cp.getString(cp.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            cp.close();
                        }
                    }

                    // get the user's photo, URI
                    InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(ctx.getContentResolver(),
                            ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(id)));

                    Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(id));
                    Uri pURI = Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);

                    Bitmap photo = null;
                    if (inputStream != null) {
                        photo = BitmapFactory.decodeStream(inputStream);
                    }

                    // if the user user has an email or phone then add it to contacts
                    if ((!TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
                            && !email.equalsIgnoreCase(name)) || (!TextUtils.isEmpty(phone))) {
                        Contact contact = new Contact();
                        contact.name = name;
                        contact.email = email;
                        contact.mobile = phone;
                        contact.profile = photo;
                        contacts.add(contact);
                    }

                } while (cursor.moveToNext());

                // clean up cursor
                cursor.close();
            }
            return contacts;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
