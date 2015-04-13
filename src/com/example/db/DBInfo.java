package com.example.db;

public class DBInfo {

    public static class DB {
        /**
         * ���ݿ�����
         */
        public static final String DB_NAME = "forever_weibo.db";
        /**
         * ���ݿ�汾
         */
        public static final int VERSION = 1;
    }

    public static class Table {
        /**
         * �û�������
         */
        public static final String USER_TABLE = "userinfo";
        /**
         * ����
         */
        public static final String _ID = "_id";
        /**
         * �û����
         */
        public static final String USER_ID = "user_id";
        /**
         * �û�����
         */
        public static final String USER_NAME = "user_name";
        /**
         * �û��Ա�
         */
        public static final String USER_GENDER = "user_gender";
        /**
         * ΢������
         */
        public static final String STATUSES_COUNT = "statuses_count";
        /**
         * ��˿����
         */
        public static final String FOLLOWERS_COUNT = "followers_count";
        /**
         * ��ע����
         */
        public static final String FRIENDS_COUNT = "friends_count";
        /**
         * token
         */
        public static final String TOKEN = "token";
        /**
         * token_secret
         */
        public static final String TOKEN_SECRET = "token_secret";
        /**
         * �û�������
         */
        public static final String DESCRIPTION = "description";
        /**
         * �û�ͷ��
         */
        public static final String USER_HEAD = "user_head";
        /**
         * �����û�����
         */
        public static final String CREATE_USER_TABLE = "create table if not exists "
                + USER_TABLE
                + "("
                + _ID
                + " integer primary key autoincrement, "
                + USER_ID
                + " text, "
                + USER_NAME + " text, "
                + USER_GENDER + " text, "
                + STATUSES_COUNT + " integer,"
                + FOLLOWERS_COUNT + " integer,"
                + FRIENDS_COUNT + " integer,"
                + TOKEN+" text, "+TOKEN_SECRET+" text, "+DESCRIPTION+" text, "+ USER_HEAD+" BLOB);";
        
        /**
         * ɾ���û���ռ���
         */
        public static final String DROP_USER_TABLE = "drop table "+USER_TABLE;

    }

}
