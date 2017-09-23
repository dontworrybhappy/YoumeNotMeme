package com.youmenotmeme;

/**
 * Created by doug on 9/23/17.
 * File to hold/access caption data (in place of a DB).
 */
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;


public class Captions {
    public static Map<String, List<CaptionPair>> captions = new HashMap<>();
    static {
        captions.put("aliens", new ArrayList<CaptionPair>(){{
            add(new CaptionPair("Who cuts my hair?","Aliens"));
        }});
        captions.put("goodGuyGreg", new ArrayList<CaptionPair>(){{
            add(new CaptionPair("Sees poor meme","Likes it anyway"));
        }});
        captions.put("jDawg", new ArrayList<CaptionPair>(){{
            add(new CaptionPair("I fancy it","when you call me large father"));
        }});
        captions.put("mostInterestingManInTheWorld", new ArrayList<CaptionPair>(){{
            add(new CaptionPair("I'm not always up before 7AM.","But when I am it's because I haven't gone to sleep."));
        }});
        captions.put("scumbagSteve", new ArrayList<CaptionPair>(){{
            add(new CaptionPair("Goes to McDonalds","Asks for Directions to Burger King"));
        }});
        captions.put("successKid", new ArrayList<CaptionPair>(){{
            add(new CaptionPair("Yes","Victory!"));
        }});
        captions.put("wat", new ArrayList<CaptionPair>(){{
            add(new CaptionPair("","wat"));
        }});
    };
}
