package dev.silvadev.blockode.beans;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.SerializedName;
import dev.silvadev.blockode.utils.PrintUtil;

public class ProjectBasicInfoBean extends BaseBean implements Parcelable {
  public static final Creator<ProjectBasicInfoBean> CREATOR =
      new Creator<ProjectBasicInfoBean>() {
        public ProjectBasicInfoBean createFromParcel(Parcel parcel) {
          return new ProjectBasicInfoBean(parcel);
        }

        public ProjectBasicInfoBean[] newArray(int size) {
          return new ProjectBasicInfoBean[size];
        }
      };

  @SerializedName("project_name")
  public String name;

  @SerializedName("package_name")
  public String packageName;

  public ProjectBasicInfoBean() {}

  public ProjectBasicInfoBean(final Parcel parcel) {
    this.name = parcel.readString();
    this.packageName = parcel.readString();
  }

  public void copy(final ProjectBasicInfoBean other) {
    this.name = other.name;
    this.packageName = other.packageName;
  }

  @Override
  public Creator getCreator() {
    return CREATOR;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void print() {
    PrintUtil.print(this.name);
    PrintUtil.print(this.packageName);
  }

  @Override
  public void writeToParcel(final Parcel parcel, final int flags) {
    parcel.writeString(this.name);
    parcel.writeString(this.packageName);
  }
}
