/*
 * Copyright 2004-2013 H2 Group. Multiple-Licensed under the H2 License,
 * Version 1.0, and under the Eclipse Public License, Version 1.0
 * (http://h2database.com/html/license.html).
 * Initial Developer: H2 Group
 */
package org.lealone.sql.mysql.dml;

import java.util.ArrayList;

import org.lealone.common.util.New;
import org.lealone.db.Procedure;
import org.lealone.db.ServerSession;
import org.lealone.db.result.Result;
import org.lealone.sql.SQLStatement;
import org.lealone.sql.mysql.StatementBase;
import org.lealone.sql.mysql.expression.Expression;
import org.lealone.sql.mysql.expression.Parameter;

/**
 * This class represents the statement
 * EXECUTE
 */
public class ExecuteProcedure extends StatementBase {

    private final ArrayList<Expression> expressions = New.arrayList();
    private Procedure procedure;

    public ExecuteProcedure(ServerSession session) {
        super(session);
    }

    public void setProcedure(Procedure procedure) {
        this.procedure = procedure;
    }

    /**
     * Set the expression at the given index.
     *
     * @param index the index (0 based)
     * @param expr the expression
     */
    public void setExpression(int index, Expression expr) {
        expressions.add(index, expr);
    }

    private void setParameters() {
        StatementBase prepared = (StatementBase) procedure.getPrepared();
        ArrayList<Parameter> params = prepared.getParameters();
        for (int i = 0; params != null && i < params.size() && i < expressions.size(); i++) {
            Expression expr = expressions.get(i);
            Parameter p = params.get(i);
            p.setValue(expr.getValue(session));
        }
    }

    @Override
    public boolean isQuery() {
        StatementBase prepared = (StatementBase) procedure.getPrepared();
        return prepared.isQuery();
    }

    @Override
    public int update() {
        setParameters();
        StatementBase prepared = (StatementBase) procedure.getPrepared();
        return prepared.update();
    }

    @Override
    public Result query(int limit) {
        setParameters();
        StatementBase prepared = (StatementBase) procedure.getPrepared();
        return prepared.query(limit);
    }

    @Override
    public boolean isTransactional() {
        return true;
    }

    @Override
    public Result queryMeta() {
        StatementBase prepared = (StatementBase) procedure.getPrepared();
        return prepared.queryMeta();
    }

    @Override
    public int getType() {
        return SQLStatement.EXECUTE;
    }

}
