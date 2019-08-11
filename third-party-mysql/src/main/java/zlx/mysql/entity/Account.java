package zlx.mysql.entity;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by ericens on 2017/3/25.
 */
@Slf4j
@Data
public class Account {
    int id;
    String name;
    double money;

}
