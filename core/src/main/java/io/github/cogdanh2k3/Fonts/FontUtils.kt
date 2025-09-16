package io.github.cogdanh2k3.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator

object FontUtils {
    fun loadCustomFont(size: Int, color: Color = Color.WHITE): BitmapFont {
        val generator = FreeTypeFontGenerator(Gdx.files.internal("fonts/font_kkk.TTF"))
        val parameter = FreeTypeFontGenerator.FreeTypeFontParameter().apply {
            this.size = size
            this.color = color

            // Bảng ký tự tiếng Việt (đủ dấu + chữ cái + số + ký tự cơ bản)
            characters = FreeTypeFontGenerator.DEFAULT_CHARS +
                "ĂÂĐÊÔƠƯăâđêôơư" +
                "ÁÀẢÃẠẤẦẨẪẬẮẰẲẴẶ" +
                "ÉÈẺẼẸẾỀỂỄỆ" +
                "ÍÌỈĨỊ" +
                "ÓÒỎÕỌỐỒỔỖỘỚỜỞỠỢ" +
                "ÚÙỦŨỤỨỪỬỮỰ" +
                "ÝỲỶỸỴ" +
                "áàảãạấầẩẫậắằẳẵặ" +
                "éèẻẽẹếềểễệ" +
                "íìỉĩị" +
                "óòỏõọốồổỗộớờởỡợ" +
                "úùủũụứừửữự" +
                "ýỳỷỹỵ"
        }
        val font = generator.generateFont(parameter)
        generator.dispose()
        return font
    }
}
