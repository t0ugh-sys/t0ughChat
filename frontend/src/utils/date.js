export function formatDate(date) {
  if (!date) return ''
  
  const d = new Date(date)
  const now = new Date()
  const diff = now - d
  
  // 如果时间差小于1分钟
  if (diff < 60 * 1000) {
    return '刚刚'
  }
  
  // 如果时间差小于1小时
  if (diff < 60 * 60 * 1000) {
    const minutes = Math.floor(diff / (60 * 1000))
    return `${minutes}分钟前`
  }
  
  // 如果时间差小于24小时
  if (diff < 24 * 60 * 60 * 1000) {
    const hours = Math.floor(diff / (60 * 60 * 1000))
    return `${hours}小时前`
  }
  
  // 如果是今年
  if (d.getFullYear() === now.getFullYear()) {
    return `${d.getMonth() + 1}月${d.getDate()}日 ${padZero(d.getHours())}:${padZero(d.getMinutes())}`
  }
  
  // 其他情况显示完整日期
  return `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日 ${padZero(d.getHours())}:${padZero(d.getMinutes())}`
}

function padZero(num) {
  return num.toString().padStart(2, '0')
} 