package io.github.cogdanh2k3.DataGame

object BossDatabase {

    val bossList = listOf(
        // ⚡ Pikachu Hắc Hoá
        BossData(
            turnAttackBoss = 50,
            name = "Pikachu Hắc Hoá",
            hp = 100,
            texturePath = "Boss/pikaBoss.png",
            introStory = listOf(
                "‘Pika... Chu...’ — giọng nói vang lên trầm thấp, đầy hận thù.",
                "Từ ánh sáng ngọt ngào của vùng Kanto, Pikachu nay đã bị bóng tối nuốt chửng.",
                "Ánh điện của nó không còn vàng rực mà chuyển sang đen tím đầy dữ tợn.",
                "Ngươi dám chạm vào sấm sét của ta ư?",
                "Ta sẽ thiêu rụi ngươi cho đến khi chỉ còn tro bụi!"
            ),
            midStory = listOf(
                "Hừm... Điện giật của ta chưa đủ làm ngươi tê liệt sao?",
                "Ta sẽ tăng điện áp lên gấp đôi! ⚡"
            ),
            defeatStory = listOf(
                "Tia sét cuối cùng... đã vụt tắt...",
                "Có lẽ... ta đã quên mất cảm giác được mỉm cười...",
                "Pika... chu..."
            )
        ),

        // 🧟 Zombie PVZ
        BossData(
            turnAttackBoss = 45,
            name = "Zombie PVZ",
            hp = 150,
            texturePath = "Boss/boss_pvz.png",
            introStory = listOf(
                "Brains brains",
                "Một bóng người lảo đảo bước ra khỏi vườn hoa héo úa.",
                "Từng chiếc lá rơi, từng bông hoa khô, báo hiệu sự hồi sinh của cơn ác mộng xanh xám.",
                "Ngươi đã hái quá nhiều hoa rồi đó, người sống.",
                "Giờ thì ta sẽ hái bộ não của ngươi!"
            ),
            midStory = listOf(
                "Cắn... chưa đủ... thêm nữa...",
                "Đám cây kia... không cứu được ngươi đâu!"
            ),
            defeatStory = listOf(
                "Không... phải... ánh sáng...",
                "Ta... chỉ muốn... yên nghỉ... cùng... vườn hoa cũ..."
            )
        ),

        // 😼 Mèo Máy Hắc Hoá
        BossData(
            turnAttackBoss = 60,
            name = "Mèo Máy Hắc Hoá",
            hp = 200,
            texturePath = "Boss/boss_dora.png",
            introStory = listOf(
                "‘Xin chào Nobita à không, nhầm người rồi nhỉ?’",
                "Một Doraemon đen ngòm bước ra từ cánh cổng thời gian rạn nứt.",
                "Đôi mắt cậu không còn hiền hậu — mà đỏ rực như than cháy.",
                "Những bảo bối trong túi giờ đã bị hắc hoá: đèn pin tiêu diệt, chong chóng tử thần, cánh cửa địa ngục.",
                "Ngươi có muốn thử xem món bảo bối nào sẽ xoá sổ ngươi trước không?"
            ),
            midStory = listOf(
                "Heh... túi thần kỳ vẫn còn vài món chưa dùng...",
                "Đừng nghĩ ngươi thắng chỉ vì ta là mèo!"
            ),
            defeatStory = listOf(
                "Nobita... cậu... còn sống chứ?",
                "Ta... đã đi quá xa rồi..."
            )
        ),

        // 🛸 Frieza
        BossData(
            turnAttackBoss = 75,
            name = "Frieza",
            hp = 200,
            texturePath = "Boss/boss_frize.png",
            introStory = listOf(
                "‘Ta là Frieza – Hoàng đế của vũ trụ!’",
                "Ánh sáng chói loà, hắn bay lơ lửng giữa không trung với nụ cười ngạo nghễ",
                "Từng hành tinh sụp đổ dưới tay ta, còn ngươi chỉ là một con kiến đang run rẩy",
                "Ta đã từng tha mạng cho Kakarot nhưng ngươi thì không có cơ hội đó đâu!",
                "Chuẩn bị chứng kiến sức mạnh vượt qua cả Super Saiyan đi!"
            ),
            midStory = listOf(
                "Hừ, sức mạnh của ta chưa đến 100%!",
                "Ngươi... sẽ không sống sót sau chiêu này đâu!"
            ),
            defeatStory = listOf(
                "Không thể nào... Ta... Frieza vĩ đại... lại thua sao?",
                "Ta sẽ... trở lại... mạnh hơn gấp bội...!"
            )
        ),

        // 🐱 Meme Cat Hắc Hoá
        BossData(
            turnAttackBoss = 80,
            name = "Meme Cat Hắc Hoá",
            hp = 250,
            texturePath = "Boss/boss_memecat.png",
            introStory = listOf(
                "Meow nhưng không còn dễ thương như trước nữa.",
                "Meme Cat đã bị virus Internet hắc hoá, biến thành quái thú kỹ thuật số.",
                "Ngươi nghe thấy không? Tiếng ‘meow’ giờ vang lên như tiếng lỗi hệ thống.",
                "Ngươi nghĩ mình là nhân vật chính à?",
                "Ha! Trong memeverse này, ta là kẻ thống trị sự vô lý!"
            ),
            midStory = listOf(
                "Lỗi 404: Ngươi không tìm thấy chiến thắng!",
                "Meow.exe... đang khởi động chế độ hỗn loạn!"
            ),
            defeatStory = listOf(
                "Meme... đã... hết trend...",
                "Nhưng... tiếng cười... của ta... vẫn vang mãi..."
            )
        )
    )

    // Lấy boss theo level
    fun getBossForLevel(level: Int): BossData {
        val index = (level) % bossList.size
        return bossList[index]
    }
}

data class BossData(
    val name: String,
    val hp: Int,
    val texturePath: String,
    val introStory: List<String> = listOf(),
    val defeatStory: List<String> = listOf(),
    val midStory: List<String> = listOf(),
    val turnAttackBoss: Int
)
