package net.anvisys.xpen.Object;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ANVISYS on 3/26/2018.
 */

public class ActivityData implements Parcelable {
    public  int ProjectID, ActivityID,EmployeeID,Expenses,Received, Advance,Balance;
    public  String ActivityName,ActivityDescription,Employee,ActivityStatus,UpdatedOn,ProjectName, ApproverName,UserImage;


    public ActivityData() {
    }

    public ActivityData(Parcel in) {
        ProjectID = in.readInt();
        ActivityID = in.readInt();
        EmployeeID = in.readInt();
        Expenses = in.readInt();
        Received = in.readInt();
        Advance = in.readInt();
        Balance = in.readInt();
        ActivityName = in.readString();
        ActivityDescription = in.readString();
        Employee = in.readString();
        ActivityStatus = in.readString();
        UpdatedOn = in.readString();
        ProjectName = in.readString();
        ApproverName = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ProjectID);
        dest.writeInt(ActivityID);
        dest.writeInt(EmployeeID);
        dest.writeInt(Expenses);
        dest.writeInt(Received);
        dest.writeInt(Advance);
        dest.writeInt(Balance);
        dest.writeString(ActivityName);
        dest.writeString(ActivityDescription);
        dest.writeString(Employee);
        dest.writeString(ActivityStatus);
        dest.writeString(UpdatedOn);
        dest.writeString(ProjectName);
        dest.writeString(ApproverName);

    }

    public static final Parcelable.Creator<ActivityData> CREATOR = new Creator<ActivityData>() {
        @Override
        public ActivityData createFromParcel(Parcel parcel) {
            return new ActivityData(parcel);
        }

        @Override
        public ActivityData[] newArray(int size) {
            return new ActivityData[size];
        }
    };
}