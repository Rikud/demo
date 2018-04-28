package com.dturan.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.*;
/**
 * Сообщение для обновления сообщения внутри ветки на форуме. Пустые параметры остаются без изменений. 
 */
@ApiModel(description = "Сообщение для обновления сообщения внутри ветки на форуме. Пустые параметры остаются без изменений. ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-03-25T22:28:04.198-07:00")

public class PostUpdate   {
  @JsonProperty("message")
  private String message = null;

  public PostUpdate message(String message) {
    this.message = message;
    return this;
  }

   /**
   * Собственно сообщение форума.
   * @return message
  **/
  @ApiModelProperty(example = "We should be afraid of the Kraken.", value = "Собственно сообщение форума.")
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
    PostUpdate postUpdate = (PostUpdate) o;
    return Objects.equals(this.message, postUpdate.message);
  }

  @Override
  public int hashCode() {
    return Objects.hash(message);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PostUpdate {\n");
    
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

