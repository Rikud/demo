package com.dturan.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.*;
/**
 * Информация о пользователе. 
 */
@ApiModel(description = "Информация о пользователе. ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-03-25T22:28:04.198-07:00")

public class UserUpdate   {
  @JsonProperty("fullname")
  private String fullname = null;

  @JsonProperty("about")
  private String about = null;

  @JsonProperty("email")
  private String email = null;

  public UserUpdate fullname(String fullname) {
    this.fullname = fullname;
    return this;
  }

   /**
   * Полное имя пользователя.
   * @return fullname
  **/
  @ApiModelProperty(example = "Captain Jack Sparrow", value = "Полное имя пользователя.")
  public String getFullname() {
    return fullname;
  }

  public void setFullname(String fullname) {
    this.fullname = fullname;
  }

  public UserUpdate about(String about) {
    this.about = about;
    return this;
  }

   /**
   * Описание пользователя.
   * @return about
  **/
  @ApiModelProperty(example = "This is the day you will always remember as the day that you almost caught Captain Jack Sparrow!", value = "Описание пользователя.")
  public String getAbout() {
    return about;
  }

  public void setAbout(String about) {
    this.about = about;
  }

  public UserUpdate email(String email) {
    this.email = email;
    return this;
  }

   /**
   * Почтовый адрес пользователя (уникальное поле).
   * @return email
  **/
  @ApiModelProperty(example = "captaina@blackpearl.sea", value = "Почтовый адрес пользователя (уникальное поле).")
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserUpdate userUpdate = (UserUpdate) o;
    return Objects.equals(this.fullname, userUpdate.fullname) &&
        Objects.equals(this.about, userUpdate.about) &&
        Objects.equals(this.email, userUpdate.email);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fullname, about, email);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserUpdate {\n");
    
    sb.append("    fullname: ").append(toIndentedString(fullname)).append("\n");
    sb.append("    about: ").append(toIndentedString(about)).append("\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

