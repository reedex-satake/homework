package homework.entity;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


/**
 * 各種エンティティモデルの基底クラス。
 * @author satake
 */
public class BaseEntity {
	/**
	 * ログ出力の際に内容がわかるようにフォーマットする。
	 */
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
}
