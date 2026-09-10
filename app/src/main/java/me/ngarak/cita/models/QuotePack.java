package me.ngarak.cita.models;

import java.util.List;

/** Editorial quote pack — curated set for seasonal / mood discovery. */
public class QuotePack {
    public String id;
    public String title;
    public String subtitle;
    public String mood;
    public boolean featured;
    public int[] months;
    public List<String> quoteKeys;
}
