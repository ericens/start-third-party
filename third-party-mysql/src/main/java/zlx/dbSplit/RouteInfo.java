package zlx.dbSplit;

import lombok.Builder;
import lombok.Data;

/**
 * 数据存取位置，应该存在哪个库，哪个表
 */


@Data
@Builder
public class RouteInfo {
    int db;
    int table;
}
