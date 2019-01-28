package net.anvisys.xpen.fragments;

import net.anvisys.xpen.Object.ActivityData;
import net.anvisys.xpen.Object.ProjectData;

public interface ListenerInterface {

    public void OnActivitySelect(ActivityData actData);

    public void OnProjectSelect (ProjectData project);
    public void OnDeSelect ();


}
