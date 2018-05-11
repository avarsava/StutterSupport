package com.avarsava.stuttersupport;

public enum DATE_RANGE {
    TODAY, WEEK, MONTH, EVER;

    public int getIntValue(){
        switch(this){
            case TODAY: return 0;
            case WEEK: return 7;
            case MONTH: return 31;
            case EVER: return Integer.MAX_VALUE;
        }
        return -1;
    }
}
