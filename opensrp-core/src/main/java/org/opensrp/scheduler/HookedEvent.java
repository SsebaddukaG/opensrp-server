package org.opensrp.scheduler;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.opensrp.domain.Event;
import org.opensrp.domain.Multimedia;

public interface HookedEvent {
    void invoke(MilestoneEvent event, Map<String, String> extraData);
    void scheduleSaveToOpenMRSMilestone( Enrollment el,List<Action> alertActions );
    void saveMultimediaToRegistry(Multimedia multimediaFile);
    void getEvent(JSONObject event);
    
}
