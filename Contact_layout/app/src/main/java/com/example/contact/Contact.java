package com.example.contact;

import java.io.Serializable;

class Contact implements Serializable {
    public static final long serialVersionUID = 20191101L;

    private long m_ID;
    private final String mFirstName;
    private final String mLastName;
    private final int mPhoheNumber;

    public Contact(long m_ID, String mFirstName, String mLastName, int mPhoheNumber) {
        this.m_ID = m_ID;
        this.mFirstName = mFirstName;
        this.mLastName = mLastName;
        this.mPhoheNumber = mPhoheNumber;
    }

    public long getM_ID() {
        return m_ID;
    }

    public String getmFirstName() {
        return mFirstName;
    }

    public String getmLastName() {
        return mLastName;
    }

    public int getmPhoheNumber() {
        return mPhoheNumber;
    }

    public void setM_ID(long m_ID) {
        this.m_ID = m_ID;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "m_ID=" + m_ID +
                ", mFirstName='" + mFirstName + '\'' +
                ", mLastName='" + mLastName + '\'' +
                ", mPhoheNumber=" + mPhoheNumber +
                '}';
    }
}
