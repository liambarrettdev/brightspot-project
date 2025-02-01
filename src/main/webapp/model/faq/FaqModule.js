import $ from 'jquery'
import bsp_utils from 'bsp-utils'

const QUESTION_SELECTOR = '.FaqQuestion-question'

export class FaqModule {

    constructor(el) {
        this.init()
    }

    init() {
        $(QUESTION_SELECTOR).click(function(e) {
            e.preventDefault()

            const $question = $(this)
            const $answer = $question.next()

            $answer.slideToggle()
            $question.toggleClass('active')
        })
    }
}

export default bsp_utils.plugin(false, 'bsp', 'faq-module', {
    '_each': function (item) {
        new FaqModule($(item))
    }
});
