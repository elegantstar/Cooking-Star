package toy.cookingstar.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

import toy.cookingstar.service.post.StatusType;

// Enum 값을 숫자로 받는 경우에 사용하는 TypeHandler
//@MappedTypes(StatusType.class)
public class StatusTypeTypeHandler implements TypeHandler<StatusType> {

    @Override
    public void setParameter(PreparedStatement ps, int i, StatusType parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.getStatus());
    }

    @Override
    public StatusType getResult(ResultSet rs, String columnName) throws SQLException {
        String status = rs.getString(columnName);
        return getStatusType(status);
    }

    @Override
    public StatusType getResult(ResultSet rs, int columnIndex) throws SQLException {
        String status = rs.getString(columnIndex);
        return getStatusType(status);
    }

    @Override
    public StatusType getResult(CallableStatement cs, int columnIndex) throws SQLException {
        String status = cs.getString(columnIndex);
        return getStatusType(status);
    }

    private StatusType getStatusType(String code) {
        switch (code) {
            case "0":
                return StatusType.POSTING;
            case "1":
                return StatusType.TEMPORARY_STORAGE;
            case "2":
                return StatusType.PRIVATE;
        }
        return null;
    }
}
