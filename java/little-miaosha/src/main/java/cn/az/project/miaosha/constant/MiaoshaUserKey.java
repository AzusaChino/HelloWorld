package cn.az.project.miaosha.constant;

/**
 * @author az
 */
public class MiaoshaUserKey extends BasePrefix {

    public static final int TOKEN_EXPIRE = 3600 * 24 * 2;

    private MiaoshaUserKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static MiaoshaUserKey token() {
        return new MiaoshaUserKey(TOKEN_EXPIRE, "tk");
    }

    public static MiaoshaUserKey getById() {
        return new MiaoshaUserKey(0, "id");
    }
}
