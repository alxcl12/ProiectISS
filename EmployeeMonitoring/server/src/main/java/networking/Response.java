/*
 *  @author albua
 *  created on 20/05/2021
 */
package networking;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class Response implements Serializable {
    public String type;
    public Object data;
}
