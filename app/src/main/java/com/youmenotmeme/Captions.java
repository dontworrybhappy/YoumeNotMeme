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
            add(new CaptionPair("Guy on other team can fix bug you spent hours on","Aliens"));
            add(new CaptionPair("44 warnings in code but still functions properly","Aliens"));
            add(new CaptionPair("Develops iOS app","Aliens"));
            add(new CaptionPair("Event Coordinator shows us picture of moon", "Aliens"));
        }});
        captions.put("goodGuyGreg", new ArrayList<CaptionPair>(){{
            add(new CaptionPair("Sees poor meme","Likes it anyway"));
            add(new CaptionPair("Notices you need another phone to test with","Lends you his"));
            add(new CaptionPair("Notices you need a marker","Lends you one of his"));
            add(new CaptionPair("Knows there is a football game","streams it live"));
        }});
        captions.put("jDawg", new ArrayList<CaptionPair>(){{
            add(new CaptionPair("I fancy it","when you maketh good memes"));
            add(new CaptionPair("Mergeth thy code","No conflicts"));
            add(new CaptionPair("For you I'd breaketh parietals","To finish thy code"));
            add(new CaptionPair("I am impressed by thy prowess","Deems no one about the Spartans"));
        }});
        captions.put("mostInterestingManInTheWorld", new ArrayList<CaptionPair>(){{
            add(new CaptionPair("I'm not always up before 7AM.","But when I am it's because I haven't gone to sleep."));
            add(new CaptionPair("I'm not always on Windows.","But when I am I am using ubuntu."));
            add(new CaptionPair("I don't always talk to girls.","But when I do it's because I want to find a female leader for my team."));
            add(new CaptionPair("I don't always go out on Friday nights.","But when I do it's because I want to win the AT&T Hackathon."));
        }});
        captions.put("scumbagSteve", new ArrayList<CaptionPair>(){{
            add(new CaptionPair("Asks lonely girl to be on team","Only wants to qualify for award."));
            add(new CaptionPair("44 errors","'looks good to me'."));
            add(new CaptionPair("Sees whiteboard","uses it as a wall"));
            add(new CaptionPair("Notices you have a computer running windows","'We should develop an iOS app'"));
        }});
        captions.put("successKid", new ArrayList<CaptionPair>(){{
            add(new CaptionPair("Pushes code","No conflicts!"));
            add(new CaptionPair("44 warnings","Still runs!"));
            add(new CaptionPair("Is not in computer science","Makes excellent UI's!"));
            add(new CaptionPair("Opens app","Does not crash!"));
        }});
        captions.put("wat", new ArrayList<CaptionPair>(){{
            add(new CaptionPair("","wat"));
            add(new CaptionPair("","Wat"));
            add(new CaptionPair("","Waht"));
            add(new CaptionPair("","WAAAAT"));
        }});
    };
}
