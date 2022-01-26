package toy.cookingstar.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumSet;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

public abstract class StatusEnumTypeHandler<E extends Enum<E> & StatusEnum> implements TypeHandler<StatusEnum> {

    private Class<E> type;

    public StatusEnumTypeHandler(Class<E> type) {
        this.type = type;
    }

    @Override
    public void setParameter(PreparedStatement ps, int i, StatusEnum parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.getStatus());
    }

    @Override
    public StatusEnum getResult(ResultSet rs, String columnName) throws SQLException {
        return getStatusEnum(rs.getString(columnName));
    }

    @Override
    public StatusEnum getResult(ResultSet rs, int columnIndex) throws SQLException {
        return getStatusEnum(rs.getString(columnIndex));
    }

    @Override
    public StatusEnum getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return getStatusEnum(cs.getString(columnIndex));
    }

    private StatusEnum getStatusEnum(String status) {
        return EnumSet.allOf(type)
                      .stream()
                      .filter(value -> value.getStatus().toLowerCase().equals(status.toLowerCase()))
                      .findFirst()
                      .orElseGet(null);
    }
}
