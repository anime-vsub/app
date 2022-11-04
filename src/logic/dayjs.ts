import dayjs from "dayjs"
import isToday from "dayjs/plugin/isToday"
import isTomorrow from "dayjs/plugin/isTomorrow"
import isYesterday from "dayjs/plugin/isYesterday"
import relativeTime from "dayjs/plugin/relativeTime"

import "dayjs/locale/vi"
import "dayjs/locale/ja"

dayjs.extend(isToday)
dayjs.extend(isTomorrow)
dayjs.extend(isYesterday)
dayjs.extend(relativeTime)

export default dayjs
