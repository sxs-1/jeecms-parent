package com.jeecms.auth.domain.querydsl;

import com.jeecms.auth.domain.LoginToken;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;

import javax.annotation.Generated;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;


/**
 * QLoginToken is a Querydsl query type for LoginToken
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QLoginToken extends EntityPathBase<LoginToken> {

    private static final long serialVersionUID = -1445329546L;

    public static final QLoginToken loginToken = new QLoginToken("loginToken");

    public final DateTimePath<java.util.Date> expireTime = createDateTime("expireTime", java.util.Date.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<Short> loginTarget = createNumber("loginTarget", Short.class);

    public final StringPath token = createString("token");

    public final StringPath username = createString("username");

    public QLoginToken(String variable) {
        super(LoginToken.class, forVariable(variable));
    }

    public QLoginToken(Path<? extends LoginToken> path) {
        super(path.getType(), path.getMetadata());
    }

    public QLoginToken(PathMetadata metadata) {
        super(LoginToken.class, metadata);
    }

}

