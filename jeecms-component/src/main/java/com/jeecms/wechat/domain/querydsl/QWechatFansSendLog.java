package com.jeecms.wechat.domain.querydsl;

import static com.querydsl.core.types.PathMetadataFactory.*;
import com.jeecms.wechat.domain.WechatFansSendLog;


import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QWechatFansSendLog is a Querydsl query type for WechatFansSendLog
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QWechatFansSendLog extends EntityPathBase<WechatFansSendLog> {

    private static final long serialVersionUID = 2116484178L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QWechatFansSendLog wechatFansSendLog = new QWechatFansSendLog("wechatFansSendLog");

    public final com.jeecms.common.base.domain.querydsl.QAbstractDomain _super = new com.jeecms.common.base.domain.querydsl.QAbstractDomain(this);

    public final StringPath appId = createString("appId");

    public final BooleanPath collect = createBoolean("collect");

    //inherited
    public final DateTimePath<java.util.Date> createTime = _super.createTime;

    //inherited
    public final StringPath createUser = _super.createUser;

    public final QWechatFans fans;

    //inherited
    public final BooleanPath hasDeleted = _super.hasDeleted;

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath mediaJson = createString("mediaJson");

    public final StringPath msgType = createString("msgType");

    public final StringPath openId = createString("openId");

    public final BooleanPath reply = createBoolean("reply");

    public final NumberPath<Integer> sendStatus = createNumber("sendStatus", Integer.class);

    public final NumberPath<Integer> sendType = createNumber("sendType", Integer.class);

    //inherited
    public final DateTimePath<java.util.Date> updateTime = _super.updateTime;

    //inherited
    public final StringPath updateUser = _super.updateUser;

    public QWechatFansSendLog(String variable) {
        this(WechatFansSendLog.class, forVariable(variable), INITS);
    }

    public QWechatFansSendLog(Path<? extends WechatFansSendLog> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QWechatFansSendLog(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QWechatFansSendLog(PathMetadata metadata, PathInits inits) {
        this(WechatFansSendLog.class, metadata, inits);
    }

    public QWechatFansSendLog(Class<? extends WechatFansSendLog> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.fans = inits.isInitialized("fans") ? new QWechatFans(forProperty("fans"), inits.get("fans")) : null;
    }

}

