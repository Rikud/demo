package com.dturan.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;
/**
 * Error
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-03-25T22:28:04.198-07:00")

public class Error   {
  @JsonProperty("message")
  private String message = null;

  public Error() {
    this.message = "";
  }

  public Error(String message) {
    this.message = message;
  }

  public Error message(String message) {
    this.message = message;
    return this;
  }

   /**
   * Текстовое описание ошибки. В процессе проверки API никаких проверок на содерижимое данного описание не делается. 
   * @return message
  **/
  @ApiModelProperty(example = "Can&#39;t find user with id #42",
          readOnly = true, value = "Текстовое описание ошибки. В процессе проверки API никаких проверок на содерижимое данного описание не делается. ")
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Error error = (Error) o;
    return Objects.equals(this.message, error.message);
  }

  @Override
  public int hashCode() {
    return Objects.hash(message);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Error {\n");
    
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
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

