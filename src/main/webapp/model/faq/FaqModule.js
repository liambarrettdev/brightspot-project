import $ from 'jquery'
import bsp_utils from 'bsp-utils'

export class FaqModule {

    constructor(el) {
        this.$el = $(el)
        this.init()
    }

    init() {
        this.$el.find('.FaqQuestion-question').on('click', (e) => {
            e.preventDefault()

            const $question = $(e.currentTarget)
            const $answer = $question.next('.FaqQuestion-answer')
            const isExpanded = $question.attr('aria-expanded') === 'true'

            $answer.slideToggle()
            $question.toggleClass('active')
            
            $question.attr('aria-expanded', !isExpanded)
            $answer.attr('aria-hidden', isExpanded)
        })
    }
}

export default bsp_utils.plugin(false, 'bsp', 'faq-module', {
    '_each': function (item) {
        new FaqModule($(item))
    }
});
