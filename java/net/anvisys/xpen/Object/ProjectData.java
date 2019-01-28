package net.anvisys.xpen.Object;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ANVISYS on 3/21/2018.
 */

public class ProjectData implements Parcelable {

    public String ProjectName,ProjectNumber,ClientName, CreationDate,Status,StatusDate, StatusRemark,Approver,ProjectDescription;
    public int ProjectID, ExpenseAmount,ReceiveAmount,ProjectValue,WorkCompletion, ApproverID;


    public ProjectData() {
    }

    public ProjectData(Parcel in) {
        ProjectID = in.readInt();

        ExpenseAmount = in.readInt();
        ReceiveAmount = in.readInt();
        ProjectValue = in.readInt();
        WorkCompletion = in.readInt();
        ProjectName = in.readString();
        ProjectNumber = in.readString();
        ClientName = in.readString();
        CreationDate = in.readString();
        Status = in.readString();
        StatusDate = in.readString();
        StatusRemark = in.readString();
        Approver = in.readString();
        ProjectDescription = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ProjectID);
        dest.writeInt(ExpenseAmount);
        dest.writeInt(ReceiveAmount);
        dest.writeInt(ProjectValue);
        dest.writeInt(WorkCompletion);
        dest.writeString(ProjectName);
        dest.writeString(ProjectNumber);
        dest.writeString(ClientName);
        dest.writeString(CreationDate);
        dest.writeString(Status);
        dest.writeString(StatusDate);
        dest.writeString(StatusRemark);
        dest.writeString(Approver);
        dest.writeString(ProjectDescription);
    }

    public static final Parcelable.Creator<ProjectData> CREATOR = new Creator<ProjectData>() {
        @Override
        public ProjectData createFromParcel(Parcel parcel) {
            return new ProjectData(parcel);
        }

        @Override
        public ProjectData[] newArray(int size) {
            return new ProjectData[size];
        }
    };
}

